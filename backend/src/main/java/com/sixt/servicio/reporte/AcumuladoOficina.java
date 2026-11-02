package com.sixt.servicio.reporte;

import com.sixt.modelo.Oficina;

// Fila del reporte por oficina: las reservas son los retiros (lo que se facturo desde ahi)
// y ademas se cuentan los autos que se devuelven en ella
public class AcumuladoOficina extends Acumulado {

    private int devoluciones;

    public AcumuladoOficina(Oficina oficina) {
        super(String.valueOf(oficina.getId()), oficina.getZona());
    }

    public void sumarDevolucion() {
        devoluciones++;
    }

    public int getDevoluciones() {
        return devoluciones;
    }
}
