package ar.edu.utn.inspt.sixt.servicio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.utn.inspt.sixt.dto.NuevaReserva;
import ar.edu.utn.inspt.sixt.excepcion.ConflictoExcepcion;
import ar.edu.utn.inspt.sixt.excepcion.DatosInvalidosExcepcion;
import ar.edu.utn.inspt.sixt.excepcion.RecursoNoEncontradoExcepcion;
import ar.edu.utn.inspt.sixt.modelo.Oficina;
import ar.edu.utn.inspt.sixt.modelo.Permiso;
import ar.edu.utn.inspt.sixt.modelo.Persona;
import ar.edu.utn.inspt.sixt.modelo.Reserva;
import ar.edu.utn.inspt.sixt.modelo.Rol;
import ar.edu.utn.inspt.sixt.modelo.Vehiculo;
import ar.edu.utn.inspt.sixt.repositorio.ReservaRepositorio;
import ar.edu.utn.inspt.sixt.servicio.precio.CalculadoraPrecio;
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
        when(oficinaServicio.buscar(any())).thenReturn(new Oficina());
        when(calculadoraPrecio.calcular(auto, DESDE, HASTA)).thenReturn(new BigDecimal("30000"));
        when(reservaRepositorio.save(any())).thenAnswer(invocacion -> invocacion.getArgument(0));

        Reserva reserva = reservaServicio.crear(pedido(DESDE, HASTA, 3L, 2L));

        assertThat(reserva.getPrecioTotal()).isEqualByComparingTo("30000");
        assertThat(reserva.getDevuelto()).isFalse();
        assertThat(reserva.getPatente()).isEqualTo("APG023");
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
        when(personaServicio.buscarActiva(2L)).thenReturn(vendedor);

        // Pepe (VENDEDOR) como cliente
        assertThatThrownBy(() -> reservaServicio.crear(pedido(DESDE, HASTA, 2L, 2L)))
                .isInstanceOf(DatosInvalidosExcepcion.class)
                .hasMessageContaining("ALQUILAR");
    }

    @Test
    void rechazaUnVendedorSinPermisoDeVender() {
        when(vehiculoServicio.buscarActivo(1L)).thenReturn(auto);
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
        reserva.setVehiculo(auto);
        reserva.setOficinaDestino(destino);
        reserva.setDevuelto(false);
        when(reservaRepositorio.findById(5L)).thenReturn(Optional.of(reserva));
        when(reservaRepositorio.save(reserva)).thenReturn(reserva);

        reservaServicio.devolver(5L);

        assertThat(reserva.getDevuelto()).isTrue();
        assertThat(auto.getOficina()).isSameAs(destino);
    }

    @Test
    void devolverUnaReservaInexistenteDaNoEncontrado() {
        when(reservaRepositorio.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservaServicio.devolver(99L))
                .isInstanceOf(RecursoNoEncontradoExcepcion.class);
    }

    private static NuevaReserva pedido(LocalDate desde, LocalDate hasta, Long clienteId, Long vendedorId) {
        return new NuevaReserva(clienteId, vendedorId, 1L, 1L, 2L, desde, hasta, 20);
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
