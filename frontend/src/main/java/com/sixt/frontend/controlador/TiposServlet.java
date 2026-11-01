package com.sixt.frontend.controlador;

import com.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

// Pestaña "Tipos y precios" del catalogo: el precio por dia de cada tipo (AUTO, CAMIONETA...)
@WebServlet("/tipos")
public class TiposServlet extends PantallaServlet {

    @Override
    protected void cargarDatos(HttpServletRequest request) throws ServletException {
        List<Map<String, Object>> tipos = ClienteBackend.getLista(request, "/api/tipos");
        request.setAttribute("tipos", tipos);
        marcarEdicion(request, tipos);
    }

    @Override
    protected String vista() {
        return "tipos.jsp";
    }

    // Con id es una edicion (PUT), sin id es un alta (POST)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, Object> datos = Formulario.campos(request, "nombre", "precio");
        ClienteBackend.Respuesta respuesta = esEdicion(request)
                ? ClienteBackend.put(request, "/api/tipos/" + request.getParameter("id"), datos)
                : ClienteBackend.post(request, "/api/tipos", datos);
        responder(respuesta, "/tipos", request, response);
    }
}
