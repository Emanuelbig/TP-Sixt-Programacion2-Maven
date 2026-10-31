package ar.edu.utn.inspt.sixt.modelo;

// Lo que una persona puede hacer en el sistema segun su rol.
// Los roles son datos (tablas rol y rol_permiso): un rol nuevo como "SYSADMIN"
// se da de alta con los permisos que corresponda, sin tocar codigo.
// Solo hace falta agregar un valor aca cuando aparece una funcionalidad nueva.
public enum Permiso {

    ADMINISTRAR("Gestiona oficinas, vehiculos, personas y roles"),
    ALQUILAR("Puede ser cliente de una reserva"),
    VENDER("Puede registrar reservas como vendedor");

    private final String descripcion;

    Permiso(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
