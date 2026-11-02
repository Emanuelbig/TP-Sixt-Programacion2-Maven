package com.sixt.seguridad.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;

// Alta y edicion de un rol. Los permisos llegan como texto: "VEHICULO:EDITAR"
public record DatosRol(
        @NotBlank(message = "El nombre del rol es obligatorio")
        @Size(max = 50, message = "El nombre del rol no puede superar los 50 caracteres")
        String nombre,

        @NotEmpty(message = "El rol tiene que tener al menos un permiso")
        Set<String> permisos) {
}
