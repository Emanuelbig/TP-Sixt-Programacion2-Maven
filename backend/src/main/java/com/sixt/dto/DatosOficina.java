package com.sixt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DatosOficina(
        @NotBlank(message = "La zona es obligatoria")
        @Size(max = 100, message = "La zona no puede superar los 100 caracteres")
        String zona,

        @NotBlank(message = "La direccion es obligatoria")
        @Size(max = 500, message = "La direccion no puede superar los 500 caracteres")
        String direccion) {
}
