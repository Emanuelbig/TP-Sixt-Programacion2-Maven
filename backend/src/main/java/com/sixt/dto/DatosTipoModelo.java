package com.sixt.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

// Lo que llega del formulario de "Tipos y precios"
public record DatosTipoModelo(
        @NotBlank(message = "El nombre del tipo es obligatorio")
        @Size(max = 50, message = "El nombre del tipo no puede superar los 50 caracteres")
        String nombre,

        // Mismo tamaño que la columna: hasta 10 enteros y 2 decimales
        @NotNull(message = "El precio por dia es obligatorio")
        @Positive(message = "El precio por dia tiene que ser mayor a cero")
        @Digits(integer = 10, fraction = 2, message = "El precio admite hasta 10 enteros y 2 decimales")
        BigDecimal precio) {
}
