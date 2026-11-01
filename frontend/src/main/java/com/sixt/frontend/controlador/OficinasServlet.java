package com.sixt.frontend.controlador;

import com.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/oficinas", "/oficinas/baja"})
public class OficinasServlet extends PantallaServlet {

    @Override
    protected void cargarDatos(HttpServletRequest request) throws ServletException {
        List<Map<String, Object>> oficinas = ClienteBackend.getLista(request, "/api/oficinas");
        request.setAttribute("oficinas", oficinas);
        marcarEdicion(request, oficinas);
    }

    @Override
    protected String vista() {
        return "oficinas.jsp";
    }

    // Baja (DELETE), edicion con id (PUT) o alta sin id (POST)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getServletPath().equals("/oficinas/baja")) {
            responder(ClienteBackend.delete(request, "/api/oficinas/" + request.getParameter("id")),
                    "/oficinas", request, response);
            return;
        }
        Map<String, Object> datos = Formulario.campos(request, "zona", "direccion");
        ClienteBackend.Respuesta respuesta = esEdicion(request)
                ? ClienteBackend.put(request, "/api/oficinas/" + request.getParameter("id"), datos)
                : ClienteBackend.post(request, "/api/oficinas", datos);
        responder(respuesta, "/oficinas", request, response);
    }
}
