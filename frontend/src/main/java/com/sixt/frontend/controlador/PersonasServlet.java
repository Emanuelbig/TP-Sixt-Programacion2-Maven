package com.sixt.frontend.controlador;

import com.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/personas", "/personas/baja"})
public class PersonasServlet extends PantallaServlet {

    @Override
    protected void cargarDatos(HttpServletRequest request) throws ServletException {
        List<Map<String, Object>> personas = ClienteBackend.getLista(request, "/api/personas");
        request.setAttribute("personas", personas);
        request.setAttribute("roles", ClienteBackend.getLista(request, "/api/roles"));
        marcarEdicion(request, personas);
    }

    @Override
    protected String vista() {
        return "personas.jsp";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ClienteBackend.Respuesta respuesta;

        if (request.getServletPath().equals("/personas/baja")) {
            respuesta = ClienteBackend.delete(request, "/api/personas/" + request.getParameter("id"));
        } else if (esEdicion(request)) {
            // Al editar no se toca la contraseña: eso lo hace cada uno desde "Mi cuenta"
            respuesta = ClienteBackend.put(request, "/api/personas/" + request.getParameter("id"),
                    Formulario.campos(request, "nombre", "dni", "direccion", "email", "telefono", "rolId"));
        } else {
            respuesta = ClienteBackend.post(request, "/api/personas",
                    Formulario.campos(request, "nombre", "dni", "direccion", "email", "telefono", "rolId", "contrasenia"));
        }
        responder(respuesta, "/personas", request, response);
    }
}
