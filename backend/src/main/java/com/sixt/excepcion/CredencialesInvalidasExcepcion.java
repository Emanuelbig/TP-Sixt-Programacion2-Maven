package com.sixt.excepcion;

// Email o contraseña incorrectos (termina en un 401).
public class CredencialesInvalidasExcepcion extends RuntimeException {

    public CredencialesInvalidasExcepcion() {
        super("Email o contraseña incorrectos");
    }
}
