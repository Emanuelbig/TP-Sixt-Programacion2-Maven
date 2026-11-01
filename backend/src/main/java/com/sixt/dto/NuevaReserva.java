package com.sixt.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.time.LocalDate;

public record NuevaReserva(
        @NotNull(message = "El cliente es obligatorio")
        Long clienteId,

        // Null si reserva el propio cliente. Quien puede mandarlo lo controla ReservaControlador
        Long vendedorId,

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

        @NotEmpty(message = "La reserva debe incluir al menos un vehiculo")
        List<@NotNull @Valid NuevoDetalleReserva> detalles) {
}
