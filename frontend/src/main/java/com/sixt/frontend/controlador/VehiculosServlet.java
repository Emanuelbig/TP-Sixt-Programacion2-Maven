package com.sixt.frontend.controlador;

import com.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/vehiculos", "/vehiculos/baja"})
public class VehiculosServlet extends PantallaServlet {

    @Override
    protected void cargarDatos(HttpServletRequest request) throws ServletException {
        List<Map<String, Object>> vehiculos = ClienteBackend.getLista(request, "/api/vehiculos");
        request.setAttribute("vehiculos", vehiculos);
        request.setAttribute("modelos", ClienteBackend.getLista(request, "/api/modelos"));
        request.setAttribute("oficinas", ClienteBackend.getLista(request, "/api/oficinas"));
        marcarEdicion(request, vehiculos);
    }

    @Override
    protected String vista() {
        return "vehiculos.jsp";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ClienteBackend.Respuesta respuesta;

        if (request.getServletPath().equals("/vehiculos/baja")) {
            respuesta = ClienteBackend.delete(request, "/api/vehiculos/" + request.getParameter("id"));
        } else {
            Map<String, Object> datos = Formulario.campos(request,
                    "patente", "color", "anio", "modeloId", "oficinaId");
            respuesta = esEdicion(request)
                    ? ClienteBackend.put(request, "/api/vehiculos/" + request.getParameter("id"), datos)
                    : ClienteBackend.post(request, "/api/vehiculos", datos);
        }
        responder(respuesta, "/vehiculos", request, response);
    }
}
