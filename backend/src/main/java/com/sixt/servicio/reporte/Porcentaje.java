package com.sixt.servicio.reporte;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Cuentas de porcentaje de los reportes, redondeadas a entero y sin dividir por cero
final class Porcentaje {

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    private Porcentaje() {
    }

    static int de(long parte, long total) {
        if (total <= 0) {
            return 0;
        }
        return de(BigDecimal.valueOf(parte), BigDecimal.valueOf(total));
    }

    static int de(BigDecimal parte, BigDecimal total) {
        if (total.signum() <= 0) {
            return 0;
        }
        return parte.multiply(CIEN).divide(total, 0, RoundingMode.HALF_UP).intValue();
    }
}
