package com.sixt.frontend.controlador;

import com.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

// Pestaña "Modelos" del catalogo: marca, nombre y tipo de cada modelo
@WebServlet("/modelos")
public class ModelosServlet extends PantallaServlet {

    @Override
    protected void cargarDatos(HttpServletRequest request) throws ServletException {
        List<Map<String, Object>> modelos = ClienteBackend.getLista(request, "/api/modelos");
        request.setAttribute("modelos", modelos);
        request.setAttribute("tipos", ClienteBackend.getLista(request, "/api/tipos"));
        marcarEdicion(request, modelos);
    }

    @Override
    protected String vista() {
        return "modelos.jsp";
    }

    // Con id es una edicion (PUT), sin id es un alta (POST)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, Object> datos = Formulario.campos(request, "marca", "nombre", "tipoId");
        ClienteBackend.Respuesta respuesta = esEdicion(request)
                ? ClienteBackend.put(request, "/api/modelos/" + request.getParameter("id"), datos)
                : ClienteBackend.post(request, "/api/modelos", datos);
        responder(respuesta, "/modelos", request, response);
    }
}
