package com.sixt.servicio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sixt.dto.NuevaReserva;
import com.sixt.dto.NuevoDetalleReserva;
import com.sixt.modelo.DetalleReserva;
import java.util.List;
import com.sixt.excepcion.ConflictoExcepcion;
import com.sixt.excepcion.DatosInvalidosExcepcion;
import com.sixt.excepcion.RecursoNoEncontradoExcepcion;
import com.sixt.modelo.Oficina;
import com.sixt.modelo.Permiso;
import com.sixt.modelo.Persona;
import com.sixt.modelo.Reserva;
import com.sixt.modelo.Rol;
import com.sixt.modelo.Vehiculo;
import com.sixt.repositorio.ReservaRepositorio;
import com.sixt.servicio.precio.CalculadoraPrecio;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Prueba solo las reglas de ReservaServicio: todo lo que usa esta simulado con Mockito
@ExtendWith(MockitoExtension.class)
class ReservaServicioTest {

    private static final LocalDate DESDE = LocalDate.of(2026, 11, 10);
    private static final LocalDate HASTA = LocalDate.of(2026, 11, 12);

    @Mock private ReservaRepositorio reservaRepositorio;
    @Mock private PersonaServicio personaServicio;
    @Mock private VehiculoServicio vehiculoServicio;
    @Mock private OficinaServicio oficinaServicio;
    @Mock private CalculadoraPrecio calculadoraPrecio;

    @InjectMocks private ReservaServicio reservaServicio;

    private final Vehiculo auto = vehiculo(1L, "APG023");
    private final Persona cliente = persona(3L, "Ema", Permiso.ALQUILAR);
    private final Persona vendedor = persona(2L, "Pepe", Permiso.VENDER);

    @Test
    void creaLaReservaConElPrecioQueDaLaCalculadora() {
        when(vehiculoServicio.buscarActivo(1L)).thenReturn(auto);
        when(personaServicio.buscarActiva(3L)).thenReturn(cliente);
        when(personaServicio.buscarActiva(2L)).thenReturn(vendedor);
        when(oficinaServicio.buscarActiva(any())).thenReturn(new Oficina());
        when(calculadoraPrecio.calcular(auto, DESDE, HASTA)).thenReturn(new BigDecimal("30000"));
        when(reservaRepositorio.save(any())).thenAnswer(invocacion -> invocacion.getArgument(0));

        Reserva reserva = reservaServicio.crear(pedido(DESDE, HASTA, 3L, 2L));

        assertThat(reserva.getPrecioTotal()).isEqualByComparingTo("30000");
        assertThat(reserva.getDevuelto()).isFalse();
        assertThat(reserva.getDetalles().getFirst().getPatente()).isEqualTo("APG023");
        assertThat(reserva.getCliente()).isSameAs(cliente);
    }

    @Test
    void rechazaFechasAlReves() {
        assertThatThrownBy(() -> reservaServicio.crear(pedido(HASTA, DESDE, 3L, 2L)))
                .isInstanceOf(DatosInvalidosExcepcion.class)
                .hasMessageContaining("fecha de fin");

        verify(reservaRepositorio, never()).save(any());
    }

    @Test
    void rechazaUnVehiculoYaReservadoEnEsasFechas() {
        when(vehiculoServicio.buscarActivo(1L)).thenReturn(auto);
        when(reservaRepositorio.vehiculoOcupadoEntre(1L, DESDE, HASTA)).thenReturn(true);

        assertThatThrownBy(() -> reservaServicio.crear(pedido(DESDE, HASTA, 3L, 2L)))
                .isInstanceOf(ConflictoExcepcion.class)
                .hasMessageContaining("APG023");

        verify(reservaRepositorio, never()).save(any());
    }

    @Test
    void rechazaUnClienteSinPermisoDeAlquilar() {
        when(vehiculoServicio.buscarActivo(1L)).thenReturn(auto);
        when(calculadoraPrecio.calcular(auto, DESDE, HASTA)).thenReturn(BigDecimal.TEN);
        when(personaServicio.buscarActiva(2L)).thenReturn(vendedor);

        // Pepe (VENDEDOR) como cliente
        assertThatThrownBy(() -> reservaServicio.crear(pedido(DESDE, HASTA, 2L, 2L)))
                .isInstanceOf(DatosInvalidosExcepcion.class)
                .hasMessageContaining("ALQUILAR");
    }

    @Test
    void rechazaUnVendedorSinPermisoDeVender() {
        when(vehiculoServicio.buscarActivo(1L)).thenReturn(auto);
        when(calculadoraPrecio.calcular(auto, DESDE, HASTA)).thenReturn(BigDecimal.TEN);
        when(personaServicio.buscarActiva(3L)).thenReturn(cliente);

        // Ema (CLIENTE) como vendedora
        assertThatThrownBy(() -> reservaServicio.crear(pedido(DESDE, HASTA, 3L, 3L)))
                .isInstanceOf(DatosInvalidosExcepcion.class)
                .hasMessageContaining("VENDER");
    }

    @Test
    void alDevolverElVehiculoQuedaEnLaOficinaDeDestino() {
        Oficina destino = new Oficina();
        Reserva reserva = new Reserva();
        DetalleReserva detalle = new DetalleReserva();
        detalle.setVehiculo(auto);
        reserva.agregarDetalle(detalle);
        Vehiculo segundo = vehiculo(2L, "AE123CD");
        DetalleReserva otro = new DetalleReserva();
        otro.setVehiculo(segundo);
        reserva.agregarDetalle(otro);
        reserva.setOficinaDestino(destino);
        reserva.setDevuelto(false);
        when(reservaRepositorio.findById(5L)).thenReturn(Optional.of(reserva));
        when(reservaRepositorio.save(reserva)).thenReturn(reserva);

        reservaServicio.devolver(5L);

        assertThat(reserva.getDevuelto()).isTrue();
        assertThat(auto.getOficina()).isSameAs(destino);
        assertThat(segundo.getOficina()).isSameAs(destino);
    }

    @Test
    void devolverUnaReservaInexistenteDaNoEncontrado() {
        when(reservaRepositorio.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservaServicio.devolver(99L))
                .isInstanceOf(RecursoNoEncontradoExcepcion.class);
    }

    @Test
    void creaVariosDetallesYSumaSusPrecios() {
        Vehiculo segundo = vehiculo(2L, "AE123CD");
        when(vehiculoServicio.buscarActivo(1L)).thenReturn(auto);
        when(vehiculoServicio.buscarActivo(2L)).thenReturn(segundo);
        when(personaServicio.buscarActiva(3L)).thenReturn(cliente);
        when(personaServicio.buscarActiva(2L)).thenReturn(vendedor);
        when(oficinaServicio.buscarActiva(any())).thenReturn(new Oficina());
        when(calculadoraPrecio.calcular(auto, DESDE, HASTA)).thenReturn(new BigDecimal("30000"));
        when(calculadoraPrecio.calcular(segundo, DESDE, HASTA)).thenReturn(new BigDecimal("54000"));
        when(reservaRepositorio.save(any())).thenAnswer(i -> i.getArgument(0));
        Reserva reserva = reservaServicio.crear(new NuevaReserva(3L, 2L, 1L, 2L, DESDE, HASTA,
                List.of(new NuevoDetalleReserva(1L, 20), new NuevoDetalleReserva(2L, 35))));
        assertThat(reserva.getPrecioTotal()).isEqualByComparingTo("84000");
        assertThat(reserva.getDetalles()).hasSize(2).allSatisfy(d -> assertThat(d.getReserva()).isSameAs(reserva));
        assertThat(reserva.getDetalles()).extracting(DetalleReserva::getLitrosNafta).containsExactly(20, 35);
        assertThat(reserva.getDetalles()).extracting(DetalleReserva::getPrecioAlquiler)
                .containsExactly(new BigDecimal("30000"), new BigDecimal("54000"));
    }

    @Test
    void rechazaReservaSinVehiculos() {
        assertThatThrownBy(() -> reservaServicio.crear(new NuevaReserva(3L, 2L, 1L, 2L, DESDE, HASTA, List.of())))
                .isInstanceOf(DatosInvalidosExcepcion.class);
        verify(reservaRepositorio, never()).save(any());
    }

    @Test
    void rechazaVehiculosRepetidos() {
        when(vehiculoServicio.buscarActivo(1L)).thenReturn(auto);
        when(calculadoraPrecio.calcular(auto, DESDE, HASTA)).thenReturn(BigDecimal.TEN);
        assertThatThrownBy(() -> reservaServicio.crear(new NuevaReserva(3L, 2L, 1L, 2L, DESDE, HASTA,
                List.of(new NuevoDetalleReserva(1L, 20), new NuevoDetalleReserva(1L, 30)))))
                .isInstanceOf(DatosInvalidosExcepcion.class).hasMessageContaining("repetirse");
        verify(reservaRepositorio, never()).save(any());
    }

    @Test
    void noGuardaSiElSegundoVehiculoEstaOcupado() {
        Vehiculo segundo = vehiculo(2L, "AE123CD");
        when(vehiculoServicio.buscarActivo(1L)).thenReturn(auto);
        when(vehiculoServicio.buscarActivo(2L)).thenReturn(segundo);
        when(calculadoraPrecio.calcular(auto, DESDE, HASTA)).thenReturn(BigDecimal.TEN);
        when(reservaRepositorio.vehiculoOcupadoEntre(1L, DESDE, HASTA)).thenReturn(false);
        when(reservaRepositorio.vehiculoOcupadoEntre(2L, DESDE, HASTA)).thenReturn(true);
        assertThatThrownBy(() -> reservaServicio.crear(new NuevaReserva(3L, 2L, 1L, 2L, DESDE, HASTA,
                List.of(new NuevoDetalleReserva(1L, 20), new NuevoDetalleReserva(2L, 30)))))
                .isInstanceOf(ConflictoExcepcion.class);
        verify(reservaRepositorio, never()).save(any());
    }

    @Test
    void rechazaCombustibleNegativo() {
        assertThatThrownBy(() -> reservaServicio.crear(new NuevaReserva(3L, 2L, 1L, 2L, DESDE, HASTA,
                List.of(new NuevoDetalleReserva(1L, -1)))))
                .isInstanceOf(DatosInvalidosExcepcion.class);
    }

    // ----- Cancelar -----

    @Test
    void cancelaUnaReservaQueTodaviaNoEmpezo() {
        Reserva reserva = reservaGuardada(LocalDate.now().plusDays(3), false);
        when(reservaRepositorio.findById(7L)).thenReturn(Optional.of(reserva));
        when(reservaRepositorio.save(reserva)).thenReturn(reserva);

        reservaServicio.cancelar(7L);

        assertThat(reserva.getFechaCancelacion()).isNotNull();
    }

    @Test
    void noCancelaUnaReservaQueYaEmpezo() {
        Reserva reserva = reservaGuardada(LocalDate.now(), false);
        when(reservaRepositorio.findById(7L)).thenReturn(Optional.of(reserva));

        assertThatThrownBy(() -> reservaServicio.cancelar(7L))
                .isInstanceOf(ConflictoExcepcion.class).hasMessageContaining("ya empezo");
        verify(reservaRepositorio, never()).save(any());
    }

    @Test
    void noCancelaUnaReservaDevuelta() {
        Reserva reserva = reservaGuardada(LocalDate.now().plusDays(3), true);
        when(reservaRepositorio.findById(7L)).thenReturn(Optional.of(reserva));

        assertThatThrownBy(() -> reservaServicio.cancelar(7L))
                .isInstanceOf(ConflictoExcepcion.class).hasMessageContaining("devuelta");
    }

    @Test
    void noDevuelveUnaReservaCancelada() {
        Reserva reserva = reservaGuardada(LocalDate.now().plusDays(3), false);
        reserva.setFechaCancelacion(java.time.Instant.now());
        when(reservaRepositorio.findById(7L)).thenReturn(Optional.of(reserva));

        assertThatThrownBy(() -> reservaServicio.devolver(7L))
                .isInstanceOf(ConflictoExcepcion.class).hasMessageContaining("cancelada");
    }

    private static Reserva reservaGuardada(LocalDate desde, boolean devuelta) {
        Reserva reserva = new Reserva();
        reserva.setId(7L);
        reserva.setFechaDesde(desde);
        reserva.setFechaHasta(desde.plusDays(2));
        reserva.setDevuelto(devuelta);
        return reserva;
    }

    private static NuevaReserva pedido(LocalDate desde, LocalDate hasta, Long clienteId, Long vendedorId) {
        return new NuevaReserva(clienteId, vendedorId, 1L, 2L, desde, hasta,
                List.of(new NuevoDetalleReserva(1L, 20)));
    }

    private static Vehiculo vehiculo(Long id, String patente) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(id);
        vehiculo.setPatente(patente);
        return vehiculo;
    }

    private static Persona persona(Long id, String nombre, Permiso permiso) {
        Rol rol = new Rol();
        rol.setNombre(permiso == Permiso.ALQUILAR ? "CLIENTE" : "VENDEDOR");
        rol.setPermisos(EnumSet.of(permiso));
        Persona persona = new Persona();
        persona.setId(id);
        persona.setNombre(nombre);
        persona.setRol(rol);
        return persona;
    }
}
