package com.sixt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambioContrasenia(
        @NotBlank(message = "La contraseña actual es obligatoria")
        String contraseniaActual,

        // BCrypt solo usa los primeros 72 bytes
        @NotBlank(message = "La contraseña nueva es obligatoria")
        @Size(min = 6, max = 72, message = "La contraseña nueva debe tener entre 6 y 72 caracteres")
        String contraseniaNueva) {
}
