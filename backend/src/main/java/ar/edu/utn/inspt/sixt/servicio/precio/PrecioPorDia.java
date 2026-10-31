package ar.edu.utn.inspt.sixt.servicio.precio;

import ar.edu.utn.inspt.sixt.modelo.Vehiculo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

// Regla actual: precio diario del tipo de modelo * cantidad de dias.
// Se cuentan los dos extremos: del 10 al 12 son 3 dias.
@Component
public class PrecioPorDia implements CalculadoraPrecio {

    @Override
    public BigDecimal calcular(Vehiculo vehiculo, LocalDate fechaDesde, LocalDate fechaHasta) {
        long dias = ChronoUnit.DAYS.between(fechaDesde, fechaHasta) + 1;
        BigDecimal precioDiario = vehiculo.getModelo().getTipo().getPrecio();
        return precioDiario.multiply(BigDecimal.valueOf(dias));
    }
}
