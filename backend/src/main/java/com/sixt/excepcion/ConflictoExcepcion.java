package com.sixt.excepcion;

// El pedido es valido pero choca con el estado actual de los datos,
// por ejemplo reservar un auto ya reservado en esas fechas (termina en un 409).
public class ConflictoExcepcion extends RuntimeException {

    public ConflictoExcepcion(String mensaje) {
        super(mensaje);
    }
}
