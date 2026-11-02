package com.sixt.servicio.reporte;

import com.sixt.modelo.Vehiculo;

// Fila del reporte por vehiculo: ademas de lo comun, el modelo y que parte del periodo estuvo alquilado
public class AcumuladoVehiculo extends Acumulado {

    private final String modelo;
    private final long diasDelPeriodo;

    public AcumuladoVehiculo(Vehiculo vehiculo, long diasDelPeriodo) {
        super(vehiculo.getPatente(), vehiculo.getPatente());
        this.modelo = vehiculo.getModelo().getMarca() + " " + vehiculo.getModelo().getNombre();
        this.diasDelPeriodo = diasDelPeriodo;
    }

    public String getModelo() {
        return modelo;
    }

    // Dias alquilado sobre dias del periodo, de 0 a 100
    public int getOcupacion() {
        return Porcentaje.de(getDiasAlquilado(), diasDelPeriodo);
    }
}
