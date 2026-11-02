package com.sixt.servicio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.sixt.excepcion.ConflictoExcepcion;
import com.sixt.modelo.Oficina;
import com.sixt.modelo.Reserva;
import com.sixt.modelo.Vehiculo;
import com.sixt.repositorio.ReservaRepositorio;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// El ejemplo de la consigna del grupo: una reserva de Oficina 1 a Oficina 2 que termina el 15/11
// deja el auto en Oficina 2; desde el 16/11 se puede retirar ahi y no en Oficina 1.
@ExtendWith(MockitoExtension.class)
class UbicacionDeFlotaTest {

    private static final LocalDate DIA_16 = LocalDate.of(2026, 11, 16);
    private static final LocalDate DIA_18 = LocalDate.of(2026, 11, 18);

    @Mock private ReservaRepositorio reservaRepositorio;
    @InjectMocks private UbicacionDeFlota ubicacion;

    private final Oficina oficina1 = oficina(1L, "Oficina 1");
    private final Oficina oficina2 = oficina(2L, "Oficina 2");
    private Vehiculo auto;

    @BeforeEach
    void autoEnOficina1() {
        auto = new Vehiculo();
        auto.setId(10L);
        auto.setPatente("APG023");
        auto.setOficina(oficina1);
    }

    @Test
    void sinReservasPendientesEstaDondeEstaHoy() {
        when(reservaRepositorio.pendientesQueTerminanAntesDe(10L, DIA_16)).thenReturn(List.of());

        assertThat(ubicacion.oficinaEn(auto, DIA_16)).isSameAs(oficina1);
    }

    @Test
    void despuesDeUnaReservaQuedaEnLaOficinaDeDevolucion() {
        when(reservaRepositorio.pendientesQueTerminanAntesDe(10L, DIA_16))
                .thenReturn(List.of(reserva(oficina1, oficina2, LocalDate.of(2026, 11, 15))));

        assertThat(ubicacion.oficinaEn(auto, DIA_16)).isSameAs(oficina2);
    }

    @Test
    void desdeEl16SeOfreceEnOficina2YNoEnOficina1() {
        when(reservaRepositorio.vehiculoOcupadoEntre(10L, DIA_16, DIA_18)).thenReturn(false);
        when(reservaRepositorio.pendientesQueTerminanAntesDe(10L, DIA_16))
                .thenReturn(List.of(reserva(oficina1, oficina2, LocalDate.of(2026, 11, 15))));
        when(reservaRepositorio.pendientesQueEmpiezanDespuesDe(10L, DIA_18)).thenReturn(List.of());

        assertThat(ubicacion.estaDisponible(auto, DIA_16, DIA_18, 2L, 2L)).isTrue();
        assertThat(ubicacion.estaDisponible(auto, DIA_16, DIA_18, 1L, 1L)).isFalse();
    }

    @Test
    void siTieneUnaReservaDespuesHayQueDevolverloDondeEmpiezaEsa() {
        // Esta en Oficina 1 y el 20/11 alguien lo retira de Oficina 1
        when(reservaRepositorio.vehiculoOcupadoEntre(10L, DIA_16, DIA_18)).thenReturn(false);
        when(reservaRepositorio.pendientesQueTerminanAntesDe(10L, DIA_16)).thenReturn(List.of());
        Reserva siguiente = reserva(oficina1, oficina2, LocalDate.of(2026, 11, 22));
        siguiente.setFechaDesde(LocalDate.of(2026, 11, 20));
        when(reservaRepositorio.pendientesQueEmpiezanDespuesDe(10L, DIA_18)).thenReturn(List.of(siguiente));

        assertThat(ubicacion.estaDisponible(auto, DIA_16, DIA_18, 1L, 1L)).isTrue();
        // Devolverlo en Oficina 2 dejaria sin auto a la reserva del 20/11
        assertThatThrownBy(() -> ubicacion.exigirDisponible(auto, DIA_16, DIA_18, 1L, 2L))
                .isInstanceOf(ConflictoExcepcion.class)
                .hasMessageContaining("Oficina 1");
    }

    @Test
    void unVehiculoOcupadoEnEsasFechasNoSeOfrece() {
        when(reservaRepositorio.vehiculoOcupadoEntre(10L, DIA_16, DIA_18)).thenReturn(true);

        assertThatThrownBy(() -> ubicacion.exigirDisponible(auto, DIA_16, DIA_18, 1L, 1L))
                .isInstanceOf(ConflictoExcepcion.class)
                .hasMessageContaining("APG023");
    }

    private static Oficina oficina(Long id, String zona) {
        Oficina oficina = new Oficina();
        oficina.setId(id);
        oficina.setZona(zona);
        return oficina;
    }

    private static Reserva reserva(Oficina origen, Oficina destino, LocalDate hasta) {
        Reserva reserva = new Reserva();
        reserva.setOficinaOrigen(origen);
        reserva.setOficinaDestino(destino);
        reserva.setFechaHasta(hasta);
        return reserva;
    }
}
