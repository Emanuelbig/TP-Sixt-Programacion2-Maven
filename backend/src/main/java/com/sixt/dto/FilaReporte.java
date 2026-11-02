package com.sixt.dto;

import java.math.BigDecimal;

// Una fila del reporte: un vehiculo, cuantas veces se reservo y cuanto facturo
public class FilaReporte {

    private final String patente;
    private int cantidadReservas;
    private BigDecimal totalFacturado;

    public FilaReporte(String patente) {
        this.patente = patente;
        this.cantidadReservas = 0;
        this.totalFacturado = BigDecimal.ZERO;
    }

    // Suma una reserva mas de este vehiculo con lo que se cobro
    public void sumar(BigDecimal precio) {
        cantidadReservas++;
        totalFacturado = totalFacturado.add(precio);
    }

    // Jackson usa estos getters para armar el JSON
    public String getPatente() {
        return patente;
    }

    public int getCantidadReservas() {
        return cantidadReservas;
    }

    public BigDecimal getTotalFacturado() {
        return totalFacturado;
    }
}
