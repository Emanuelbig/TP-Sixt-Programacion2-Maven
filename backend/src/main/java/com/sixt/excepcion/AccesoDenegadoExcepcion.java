package com.sixt.excepcion;

public class AccesoDenegadoExcepcion extends RuntimeException {
    public AccesoDenegadoExcepcion() {
        super("No tenes permiso para realizar esta operacion");
    }
}
