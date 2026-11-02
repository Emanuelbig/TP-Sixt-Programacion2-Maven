package com.sixt.servicio.reporte;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Una fila de un reporte: algo (un vehiculo, una oficina, un vendedor, un mes) con sus reservas,
// los dias alquilados y lo facturado. Las subclases agregan lo propio de cada reporte.
// Se ordena de mayor a menor facturacion.
public class Acumulado implements Comparable<Acumulado> {

    private final String clave;
    private final String descripcion;
    private int reservas;
    private long diasAlquilado;
    private BigDecimal facturado = BigDecimal.ZERO;

    public Acumulado(String clave, String descripcion) {
        this.clave = clave;
        this.descripcion = descripcion;
    }

    public void sumarReserva(BigDecimal monto) {
        reservas++;
        facturado = facturado.add(monto);
    }

    public void sumarDias(long dias) {
        diasAlquilado += dias;
    }

    public String getClave() {
        return clave;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getReservas() {
        return reservas;
    }

    public long getDiasAlquilado() {
        return diasAlquilado;
    }

    public BigDecimal getFacturado() {
        return facturado;
    }

    // Lo que se facturo, en promedio, por cada reserva
    public BigDecimal getPromedio() {
        if (reservas == 0) {
            return BigDecimal.ZERO;
        }
        return facturado.divide(BigDecimal.valueOf(reservas), 2, RoundingMode.HALF_UP);
    }

    @Override
    public int compareTo(Acumulado otro) {
        return otro.facturado.compareTo(facturado);
    }
}
