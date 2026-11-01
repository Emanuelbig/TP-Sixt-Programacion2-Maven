package com.sixt.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DatosVehiculo(
        // Formatos argentinos: viejo (ABC123) o Mercosur (AB123CD)
        @NotBlank(message = "La patente es obligatoria")
        @Pattern(regexp = "[A-Za-z]{3}\\d{3}|[A-Za-z]{2}\\d{3}[A-Za-z]{2}",
                message = "La patente debe tener el formato ABC123 o AB123CD")
        String patente,

        @NotBlank(message = "El color es obligatorio")
        @Size(max = 50, message = "El color no puede superar los 50 caracteres")
        String color,

        // El maximo depende del año actual, lo controla VehiculoServicio
        @NotNull(message = "El año es obligatorio")
        @Min(value = 1950, message = "El año no puede ser anterior a 1950")
        Integer anio,

        @NotNull(message = "El modelo es obligatorio")
        Long modeloId,

        @NotNull(message = "La oficina es obligatoria")
        Long oficinaId) {
}
