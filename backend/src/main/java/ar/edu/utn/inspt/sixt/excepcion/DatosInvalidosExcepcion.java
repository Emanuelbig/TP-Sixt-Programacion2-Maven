package ar.edu.utn.inspt.sixt.excepcion;

// Los datos recibidos no cumplen una regla de negocio (termina en un 400).
public class DatosInvalidosExcepcion extends RuntimeException {

    public DatosInvalidosExcepcion(String mensaje) {
        super(mensaje);
    }
}
