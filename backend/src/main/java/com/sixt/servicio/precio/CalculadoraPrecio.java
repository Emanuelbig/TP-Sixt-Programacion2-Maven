package com.sixt.servicio.precio;

import com.sixt.modelo.Vehiculo;
import java.math.BigDecimal;
import java.time.LocalDate;

// Regla para calcular cuanto cuesta una reserva.
// ReservaServicio depende de esta interfaz y no de una regla concreta: para cambiar
// la forma de cobrar (descuento por semana, tarifa de temporada, ...) se crea otra
// clase que la implemente y se marca con @Primary, sin tocar ReservaServicio.
public interface CalculadoraPrecio {

    BigDecimal calcular(Vehiculo vehiculo, LocalDate fechaDesde, LocalDate fechaHasta);
}
