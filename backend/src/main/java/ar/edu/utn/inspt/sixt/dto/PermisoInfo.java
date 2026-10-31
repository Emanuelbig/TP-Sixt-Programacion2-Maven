package ar.edu.utn.inspt.sixt.dto;

import ar.edu.utn.inspt.sixt.modelo.Permiso;

// Un permiso con su descripcion, para mostrarlo en pantalla
public record PermisoInfo(String codigo, String descripcion) {

    public static PermisoInfo de(Permiso permiso) {
        return new PermisoInfo(permiso.name(), permiso.getDescripcion());
    }
}
