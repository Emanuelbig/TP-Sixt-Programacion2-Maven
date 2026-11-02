package com.sixt.excepcion;

// Quien hace el pedido no tiene permiso para esta operacion (termina en un 403).
public class AccesoDenegadoExcepcion extends RuntimeException {
    public AccesoDenegadoExcepcion() {
        super("No tenes permiso para realizar esta operacion");
    }

    public AccesoDenegadoExcepcion(String mensaje) {
        super(mensaje);
    }
}
