package com.sixt.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record NuevoDetalleReserva(
        @NotNull(message = "El vehiculo es obligatorio") @Positive Long vehiculoId,
        @NotNull(message = "Los litros de nafta son obligatorios")
        @PositiveOrZero(message = "Los litros de nafta no pueden ser negativos") Integer litrosNafta) {
}
