package ar.edu.utn.inspt.sixt.servicio.precio;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.utn.inspt.sixt.modelo.Modelo;
import ar.edu.utn.inspt.sixt.modelo.TipoModelo;
import ar.edu.utn.inspt.sixt.modelo.Vehiculo;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class PrecioPorDiaTest {

    private final PrecioPorDia calculadora = new PrecioPorDia();

    @Test
    void cobraElPrecioDiarioPorCadaDiaContandoLosDosExtremos() {
        Vehiculo auto = vehiculoConPrecioDiario("10000.00");

        BigDecimal precio = calculadora.calcular(auto, LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12));

        assertThat(precio).isEqualByComparingTo("30000");
    }

    @Test
    void unaReservaDeUnSoloDiaCobraUnDia() {
        Vehiculo auto = vehiculoConPrecioDiario("10000.00");
        LocalDate hoy = LocalDate.of(2026, 11, 10);

        assertThat(calculadora.calcular(auto, hoy, hoy)).isEqualByComparingTo("10000");
    }

    @Test
    void noPierdeLosCentavos() {
        Vehiculo auto = vehiculoConPrecioDiario("9999.99");

        BigDecimal precio = calculadora.calcular(auto, LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 11));

        assertThat(precio).isEqualByComparingTo("19999.98");
    }

    private static Vehiculo vehiculoConPrecioDiario(String precio) {
        TipoModelo tipo = new TipoModelo();
        tipo.setPrecio(new BigDecimal(precio));
        Modelo modelo = new Modelo();
        modelo.setTipo(tipo);
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setModelo(modelo);
        return vehiculo;
    }
}
