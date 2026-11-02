package com.sixt.seguridad.modelo;

import static com.sixt.seguridad.modelo.Accion.ADMINISTRAR;
import static com.sixt.seguridad.modelo.Accion.ALQUILAR;
import static com.sixt.seguridad.modelo.Accion.CREAR;
import static com.sixt.seguridad.modelo.Accion.EDITAR;
import static com.sixt.seguridad.modelo.Accion.ELIMINAR;
import static com.sixt.seguridad.modelo.Accion.VENDER;
import static com.sixt.seguridad.modelo.Accion.VER;
import static com.sixt.seguridad.modelo.Accion.VER_PROPIAS;

import java.util.EnumSet;
import java.util.Set;

// Cada cosa del sistema que se puede proteger, con las acciones que tienen sentido sobre ella.
// Un permiso nuevo implica codigo nuevo que lo controle, por eso vive aca y no en la base.
public enum Recurso {

    OFICINA("Oficinas", VER, CREAR, EDITAR, ELIMINAR, ADMINISTRAR),
    VEHICULO("Vehiculos", VER, CREAR, EDITAR, ELIMINAR, ADMINISTRAR),
    CLIENTE("Clientes", VER, CREAR, EDITAR, ELIMINAR, ADMINISTRAR),
    USUARIO("Usuarios internos", VER, CREAR, EDITAR, ELIMINAR, ADMINISTRAR),
    RESERVA("Reservas", VER_PROPIAS, VER, CREAR, EDITAR, ADMINISTRAR, ALQUILAR, VENDER),
    ROL("Roles y permisos", VER, CREAR, EDITAR, ELIMINAR, ADMINISTRAR),
    // Los reportes son de solo lectura
    REPORTE("Reportes de reservas", VER);

    private final String descripcion;
    private final Set<Accion> acciones;

    Recurso(String descripcion, Accion primera, Accion... resto) {
        this.descripcion = descripcion;
        this.acciones = EnumSet.of(primera, resto);
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Set<Accion> getAcciones() {
        return EnumSet.copyOf(acciones);
    }

    public boolean admite(Accion accion) {
        return acciones.contains(accion);
    }
}
