package ar.edu.utn.inspt.sixt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// Alta de una persona (POST): los mismos datos que DatosPersona mas la contraseña inicial
public record NuevaPersona(
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
        Long rolId,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, max = 72, message = "La contraseña debe tener entre 6 y 72 caracteres")
        String contrasenia) {

    public DatosPersona datos() {
        return new DatosPersona(dni, nombre, email, telefono, rolId);
    }
}
