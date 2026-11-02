package com.sixt.servicio.reporte;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;

// El reporte completo de un periodo: los indicadores de arriba y las cuatro pestañas.
// Solo se lee (no tiene setters): lo arma ReporteServicio y Jackson lo pasa a JSON.
@Getter
public class ReporteReservas {

    private final LocalDate desde;
    private final LocalDate hasta;
    private final BigDecimal facturado;
    private final int reservas;
    private final BigDecimal promedio;
    private final int ocupacion;
    private final long diasAlquilado;
    private final long canceladas;
    private final List<AcumuladoVehiculo> porVehiculo;
    private final List<AcumuladoOficina> porOficina;
    private final List<Acumulado> porVendedor;
    private final List<AcumuladoMes> porMes;

    public ReporteReservas(Periodo periodo, Acumulado total, int ocupacion, long canceladas,
                           List<AcumuladoVehiculo> porVehiculo, List<AcumuladoOficina> porOficina,
                           List<Acumulado> porVendedor, List<AcumuladoMes> porMes) {
        this.desde = periodo.getDesde();
        this.hasta = periodo.getHasta();
        this.facturado = total.getFacturado();
        this.reservas = total.getReservas();
        this.promedio = total.getPromedio();
        this.diasAlquilado = total.getDiasAlquilado();
        this.ocupacion = ocupacion;
        this.canceladas = canceladas;
        this.porVehiculo = porVehiculo;
        this.porOficina = porOficina;
        this.porVendedor = porVendedor;
        this.porMes = porMes;
    }
}
