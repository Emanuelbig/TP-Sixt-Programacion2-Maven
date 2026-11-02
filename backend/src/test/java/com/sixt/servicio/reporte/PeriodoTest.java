package com.sixt.servicio.reporte;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sixt.excepcion.DatosInvalidosExcepcion;
import java.time.LocalDate;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;

class PeriodoTest {

    private final Periodo noviembre = new Periodo(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30));

    @Test
    void cuentaLosDiasConLosDosExtremos() {
        assertThat(noviembre.dias()).isEqualTo(30);
        assertThat(new Periodo(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 10)).dias()).isEqualTo(1);
    }

    @Test
    void sabeSiUnaFechaCaeAdentro() {
        assertThat(noviembre.incluye(LocalDate.of(2026, 11, 1))).isTrue();
        assertThat(noviembre.incluye(LocalDate.of(2026, 11, 30))).isTrue();
        assertThat(noviembre.incluye(LocalDate.of(2026, 12, 1))).isFalse();
    }

    @Test
    void cuentaSoloLosDiasDeUnaReservaQueCaenEnElPeriodo() {
        // Del 28/11 al 3/12: solo 28, 29 y 30 son de noviembre
        assertThat(noviembre.diasEnComun(LocalDate.of(2026, 11, 28), LocalDate.of(2026, 12, 3))).isEqualTo(3);
        assertThat(noviembre.diasEnComun(LocalDate.of(2026, 10, 30), LocalDate.of(2026, 11, 2))).isEqualTo(2);
        assertThat(noviembre.diasEnComun(LocalDate.of(2026, 12, 5), LocalDate.of(2026, 12, 8))).isZero();
    }

    @Test
    void listaLosMesesQueToca() {
        Periodo periodo = new Periodo(LocalDate.of(2026, 11, 15), LocalDate.of(2027, 2, 1));
        assertThat(periodo.meses()).containsExactly(
                YearMonth.of(2026, 11), YearMonth.of(2026, 12), YearMonth.of(2027, 1), YearMonth.of(2027, 2));
        assertThat(Periodo.anioDe(LocalDate.of(2028, 6, 15)).dias()).isEqualTo(366);
    }

    @Test
    void rechazaUnPeriodoAlReves() {
        assertThatThrownBy(() -> new Periodo(LocalDate.of(2026, 11, 30), LocalDate.of(2026, 11, 1)))
                .isInstanceOf(DatosInvalidosExcepcion.class);
    }
}
