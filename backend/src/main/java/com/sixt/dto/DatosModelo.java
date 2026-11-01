package com.sixt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Lo que llega del formulario de "Modelos"
public record DatosModelo(
        @NotBlank(message = "La marca es obligatoria")
        @Size(max = 50, message = "La marca no puede superar los 50 caracteres")
        String marca,

        @NotBlank(message = "El nombre del modelo es obligatorio")
        @Size(max = 50, message = "El nombre del modelo no puede superar los 50 caracteres")
        String nombre,

        @NotNull(message = "El tipo es obligatorio")
        Long tipoId) {
}
