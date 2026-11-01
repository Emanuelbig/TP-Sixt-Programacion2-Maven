package com.sixt.dto;

import com.sixt.modelo.Permiso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record NuevoRol(
        @NotBlank(message = "El nombre del rol es obligatorio")
        @Size(max = 50, message = "El nombre del rol no puede superar los 50 caracteres")
        String nombre,

        @NotEmpty(message = "El rol tiene que tener al menos un permiso")
        Set<Permiso> permisos) {
}
