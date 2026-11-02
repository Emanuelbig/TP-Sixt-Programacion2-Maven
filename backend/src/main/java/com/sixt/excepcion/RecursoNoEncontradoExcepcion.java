package com.sixt.excepcion;

// Se pidio algo que no existe en la base (termina en un 404).
public class RecursoNoEncontradoExcepcion extends RuntimeException {

    public RecursoNoEncontradoExcepcion(String recurso, Long id) {
        super("No existe " + recurso + " con id " + id);
    }

    public RecursoNoEncontradoExcepcion(String mensaje) {
        super(mensaje);
    }
}
