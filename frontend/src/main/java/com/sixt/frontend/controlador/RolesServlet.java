package com.sixt.frontend.controlador;

import com.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/roles")
public class RolesServlet extends PantallaServlet {

    @Override
    protected void cargarDatos(HttpServletRequest request) throws ServletException {
        request.setAttribute("roles", ClienteBackend.getLista(request, "/api/roles"));
        request.setAttribute("permisos", ClienteBackend.getLista(request, "/api/permisos"));
    }

    @Override
    protected String vista() {
        return "roles.jsp";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, Object> datos = Formulario.campos(request, "nombre");
        datos.put("permisos", Formulario.lista(request, "permisos"));
        responder(ClienteBackend.post(request, "/api/roles", datos), "/roles", request, response);
    }
}
