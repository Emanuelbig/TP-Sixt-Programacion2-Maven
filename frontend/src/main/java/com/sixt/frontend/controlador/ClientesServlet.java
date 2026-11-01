package com.sixt.frontend.controlador;

import com.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/clientes", "/clientes/baja"})
public class ClientesServlet extends PantallaServlet {

    @Override
    protected void cargarDatos(HttpServletRequest request) throws ServletException {
        List<Map<String, Object>> personas = ClienteBackend.getLista(request, "/api/clientes");
        request.setAttribute("personas", personas);
        marcarEdicion(request, personas);
    }

    @Override
    protected String vista() {
        return "clientes.jsp";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ClienteBackend.Respuesta respuesta;

        if (request.getServletPath().equals("/clientes/baja")) {
            respuesta = ClienteBackend.delete(request, "/api/clientes/" + request.getParameter("id"));
        } else if (esEdicion(request)) {
            // Al editar no se toca la contraseña: eso lo hace cada uno desde "Mi cuenta"
            respuesta = ClienteBackend.put(request, "/api/clientes/" + request.getParameter("id"),
                    Formulario.campos(request, "nombre", "dni", "direccion", "email", "telefono"));
        } else {
            respuesta = ClienteBackend.post(request, "/api/clientes",
                    Formulario.campos(request, "nombre", "dni", "direccion", "email", "telefono", "contrasenia"));
        }
        responder(respuesta, "/clientes", request, response);
    }
}
