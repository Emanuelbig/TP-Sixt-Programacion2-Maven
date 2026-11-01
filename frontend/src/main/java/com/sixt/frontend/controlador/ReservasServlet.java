package com.sixt.frontend.controlador;

import com.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@WebServlet(urlPatterns = {"/reservas", "/reservas/devolver"})
public class ReservasServlet extends PantallaServlet {

    @Override
    protected void cargarDatos(HttpServletRequest request) throws ServletException {
        request.setAttribute("reservas", ClienteBackend.getLista("/api/reservas"));
        request.setAttribute("personas", ClienteBackend.getLista("/api/personas"));
        request.setAttribute("vehiculos", ClienteBackend.getLista("/api/vehiculos"));
        request.setAttribute("oficinas", ClienteBackend.getLista("/api/oficinas"));
    }

    @Override
    protected String vista() {
        return "reservas.jsp";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ClienteBackend.Respuesta respuesta;

        if (request.getServletPath().equals("/reservas/devolver")) {
            respuesta = ClienteBackend.post("/api/reservas/" + request.getParameter("id") + "/devolver", Map.of());
        } else {
            Map<String, Object> datos = Formulario.campos(request,
                    "clienteId", "vendedorId", "oficinaOrigenId", "oficinaDestinoId",
                    "fechaDesde", "fechaHasta");
            List<Map<String, Object>> detalles = new ArrayList<>();
            for (String vehiculoId : Formulario.lista(request, "vehiculoId")) {
                String litros = request.getParameter("litrosNafta_" + vehiculoId);
                Map<String, Object> detalle = new LinkedHashMap<>();
                detalle.put("vehiculoId", vehiculoId);
                detalle.put("litrosNafta", litros == null || litros.isBlank() ? null : litros.trim());
                detalles.add(detalle);
            }
            datos.put("detalles", detalles);
            respuesta = ClienteBackend.post("/api/reservas", datos);
        }
        responder(respuesta, "/reservas", request, response);
    }
}
