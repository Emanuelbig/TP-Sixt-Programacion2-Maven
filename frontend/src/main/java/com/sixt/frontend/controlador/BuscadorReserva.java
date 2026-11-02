package com.sixt.frontend.controlador;

import com.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Lo comun de reservar en dos pasos, para el vendedor (/reservas) y el cliente (/reservar):
//  1. buscar: oficinas y fechas -> el backend devuelve los vehiculos libres en ese rango
//     que van a estar en la oficina de retiro (y que se pueden devolver en la elegida)
//  2. reservar: los vehiculos elegidos, con los litros de nafta de cada uno
final class BuscadorReserva {

    private BuscadorReserva() {
    }

    // Carga las oficinas y, si ya se eligieron fechas validas, los vehiculos disponibles
    static void cargar(HttpServletRequest request) throws ServletException {
        request.setAttribute("oficinas", ClienteBackend.getLista(request, "/api/oficinas"));
        request.setAttribute("hoy", LocalDate.now().toString());

        String desde = request.getParameter("fechaDesde");
        String hasta = request.getParameter("fechaHasta");
        if (desde == null || desde.isBlank() || hasta == null || hasta.isBlank()) {
            return; // Todavia no buscó: se muestra solo el buscador
        }
        try {
            if (LocalDate.parse(hasta).isBefore(LocalDate.parse(desde))) {
                request.setAttribute("error", "La fecha de devolución no puede ser anterior a la de retiro");
                return;
            }
        } catch (DateTimeParseException e) {
            request.setAttribute("error", "Las fechas no tienen un formato válido");
            return;
        }
        String retiro = request.getParameter("oficinaOrigenId");
        String devolucion = request.getParameter("oficinaDestinoId");
        if (retiro == null || retiro.isBlank() || devolucion == null || devolucion.isBlank()) {
            request.setAttribute("error", "Elegí la oficina de retiro y la de devolución");
            return;
        }
        request.setAttribute("disponibles", ClienteBackend.getLista(request, "/api/vehiculos/disponibles?desde="
                + desde + "&hasta=" + hasta + "&oficinaOrigenId=" + retiro + "&oficinaDestinoId=" + devolucion));
    }

    // Arma el JSON de la reserva con lo que vino del formulario. Cliente y vendedor los agrega cada servlet
    static Map<String, Object> datos(HttpServletRequest request) {
        Map<String, Object> datos = Formulario.campos(request,
                "oficinaOrigenId", "oficinaDestinoId", "fechaDesde", "fechaHasta");
        List<Map<String, Object>> detalles = new ArrayList<>();
        for (String vehiculoId : Formulario.lista(request, "vehiculoId")) {
            // Cada vehiculo tiene su propio campo de litros: litrosNafta_<id>
            String litros = request.getParameter("litrosNafta_" + vehiculoId);
            Map<String, Object> detalle = new LinkedHashMap<>();
            detalle.put("vehiculoId", vehiculoId);
            detalle.put("litrosNafta", litros == null || litros.isBlank() ? null : litros.trim());
            detalles.add(detalle);
        }
        datos.put("detalles", detalles);
        return datos;
    }
}
