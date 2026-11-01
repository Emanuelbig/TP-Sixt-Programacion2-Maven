package com.sixt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// Datos editables de una persona (PUT). La contraseña se cambia aparte (PATCH).
public record DatosPersona(
        @NotNull(message = "El DNI es obligatorio")
        @Positive(message = "El DNI debe ser un numero positivo")
        @Max(value = 99_999_999, message = "El DNI no puede tener mas de 8 digitos")
        Integer dni,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato valido")
        String email,

        @NotBlank(message = "El telefono es obligatorio")
        @Pattern(regexp = "\\d{6,15}", message = "El telefono debe tener solo numeros, entre 6 y 15 digitos")
        String telefono,

        @NotNull(message = "El rol es obligatorio")
        Long rolId) {
}
