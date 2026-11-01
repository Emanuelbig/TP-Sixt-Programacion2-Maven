package com.sixt.frontend.controlador;

import com.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/roles", "/roles/baja"})
public class RolesServlet extends PantallaServlet {

    @Override
    protected void cargarDatos(HttpServletRequest request) throws ServletException {
        List<Map<String, Object>> roles = ClienteBackend.getLista(request, "/api/roles");
        request.setAttribute("roles", roles);
        request.setAttribute("permisos", ClienteBackend.getLista(request, "/api/permisos"));
        marcarEdicion(request, roles);
    }

    @Override
    protected String vista() {
        return "roles.jsp";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ClienteBackend.Respuesta respuesta;
        if (request.getServletPath().equals("/roles/baja")) {
            respuesta = ClienteBackend.delete(request, "/api/roles/" + request.getParameter("id"));
        } else {
            Map<String, Object> datos = Formulario.campos(request, "nombre");
            datos.put("permisos", Formulario.lista(request, "permisos"));
            respuesta = esEdicion(request)
                    ? ClienteBackend.put(request, "/api/roles/" + request.getParameter("id"), datos)
                    : ClienteBackend.post(request, "/api/roles", datos);
        }
        responder(respuesta, "/roles", request, response);
    }
}
