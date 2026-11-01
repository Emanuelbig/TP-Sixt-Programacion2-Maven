package com.sixt.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

public record NuevaReserva(
        @NotNull(message = "El cliente es obligatorio")
        Long clienteId,

        @NotNull(message = "El vendedor es obligatorio")
        Long vendedorId,

        @NotNull(message = "El vehiculo es obligatorio")
        Long vehiculoId,

        @NotNull(message = "La oficina de origen es obligatoria")
        Long oficinaOrigenId,

        @NotNull(message = "La oficina de destino es obligatoria")
        Long oficinaDestinoId,

        @NotNull(message = "La fecha de inicio es obligatoria")
        @FutureOrPresent(message = "La fecha de inicio no puede ser anterior a hoy")
        LocalDate fechaDesde,

        // Que no sea anterior a fechaDesde lo controla ReservaServicio
        @NotNull(message = "La fecha de fin es obligatoria")
        LocalDate fechaHasta,

        @NotNull(message = "Los litros de nafta son obligatorios")
        @PositiveOrZero(message = "Los litros de nafta no pueden ser negativos")
        Integer litrosNafta) {
}
