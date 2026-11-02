package com.sixt.servicio.reporte;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

// Fila del reporte por mes: ademas de lo comun, el largo de su barra en el grafico
// (el mes que mas facturo ocupa el 100 %, el resto en proporcion)
public class AcumuladoMes extends Acumulado {

    private static final Locale ESPANIOL = Locale.forLanguageTag("es-AR");

    private int porcentajeBarra;

    public AcumuladoMes(YearMonth mes) {
        super(mes.toString(), mes.getMonth().getDisplayName(TextStyle.FULL, ESPANIOL) + " " + mes.getYear());
    }

    public void ajustarBarra(BigDecimal facturacionMaxima) {
        porcentajeBarra = Porcentaje.de(getFacturado(), facturacionMaxima);
    }

    public int getPorcentajeBarra() {
        return porcentajeBarra;
    }
}
