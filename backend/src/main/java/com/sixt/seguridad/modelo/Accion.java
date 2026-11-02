package com.sixt.seguridad.modelo;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

// Lo que se puede hacer sobre un recurso. Las acciones forman una cascada:
// ADMINISTRAR incluye CREAR, EDITAR y ELIMINAR; esas incluyen VER; VER incluye VER_PROPIAS.
// La cascada es de acciones y no de roles: los roles son datos que cambia el sysadmin.
public enum Accion {

    VER_PROPIAS("Ver solo lo propio"),
    VER("Ver", VER_PROPIAS),
    CREAR("Crear", VER),
    EDITAR("Editar", VER),
    ELIMINAR("Eliminar", VER),
    ADMINISTRAR("Administrar (incluye todas las anteriores)", CREAR, EDITAR, ELIMINAR),

    // Capacidades: dicen que papel puede cumplir alguien en una reserva.
    // Ninguna otra accion las incluye y no dan acceso a pantallas.
    ALQUILAR("Puede ser cliente de una reserva"),
    VENDER("Puede ser vendedor de una reserva");

    private final String descripcion;
    private final Set<Accion> directas;

    Accion(String descripcion, Accion... directas) {
        this.descripcion = descripcion;
        this.directas = directas.length == 0 ? Collections.emptySet() : Set.of(directas);
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean esCapacidad() {
        return this == ALQUILAR || this == VENDER;
    }

    // Esta accion mas todas las que incluye, directa o indirectamente
    public Set<Accion> incluidas() {
        Set<Accion> resultado = EnumSet.of(this);
        for (Accion directa : directas) {
            resultado.addAll(directa.incluidas());
        }
        return resultado;
    }
}
