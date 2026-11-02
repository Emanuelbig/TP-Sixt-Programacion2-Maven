package com.sixt.servicio.reporte;

import com.sixt.excepcion.DatosInvalidosExcepcion;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

// El rango de fechas de un reporte, con los dos extremos incluidos (como el precio de una reserva:
// del 10 al 12 son 3 dias). Es inmutable: una vez creado no cambia.
public final class Periodo {

    private final LocalDate desde;
    private final LocalDate hasta;

    public Periodo(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new DatosInvalidosExcepcion("El periodo necesita fecha de inicio y de fin");
        }
        if (hasta.isBefore(desde)) {
            throw new DatosInvalidosExcepcion("La fecha de fin del periodo no puede ser anterior a la de inicio");
        }
        this.desde = desde;
        this.hasta = hasta;
    }

    // Del 1 de enero al 31 de diciembre del año de esa fecha
    public static Periodo anioDe(LocalDate fecha) {
        return new Periodo(fecha.withDayOfYear(1), fecha.withDayOfYear(fecha.lengthOfYear()));
    }

    public LocalDate getDesde() {
        return desde;
    }

    public LocalDate getHasta() {
        return hasta;
    }

    public long dias() {
        return ChronoUnit.DAYS.between(desde, hasta) + 1;
    }

    public boolean incluye(LocalDate fecha) {
        return !fecha.isBefore(desde) && !fecha.isAfter(hasta);
    }

    // Cuantos dias de otro rango caen dentro del periodo (0 si no se tocan)
    public long diasEnComun(LocalDate otroDesde, LocalDate otroHasta) {
        LocalDate inicio = otroDesde.isAfter(desde) ? otroDesde : desde;
        LocalDate fin = otroHasta.isBefore(hasta) ? otroHasta : hasta;
        if (fin.isBefore(inicio)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(inicio, fin) + 1;
    }

    // Los meses que toca el periodo, en orden
    public List<YearMonth> meses() {
        List<YearMonth> meses = new ArrayList<>();
        YearMonth ultimo = YearMonth.from(hasta);
        for (YearMonth mes = YearMonth.from(desde); !mes.isAfter(ultimo); mes = mes.plusMonths(1)) {
            meses.add(mes);
        }
        return meses;
    }
}
