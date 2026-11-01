package com.sixt.frontend.controlador;

import com.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

// Datos de quien inicio sesion y cambio de contraseña (PATCH)
@WebServlet("/mi-cuenta")
public class MiCuentaServlet extends PantallaServlet {

    @Override
    protected void cargarDatos(HttpServletRequest request) {
        if (request.getParameter("ok") != null) {
            request.setAttribute("mensaje", "La contraseña se cambió correctamente");
        }
    }

    @Override
    protected String vista() {
        return "mi-cuenta.jsp";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Object id = UsuarioEnSesion.id(request);
        ClienteBackend.Respuesta respuesta = ClienteBackend.patch("/api/personas/" + id + "/contrasenia",
                Formulario.campos(request, "contraseniaActual", "contraseniaNueva"));
        responder(respuesta, "/mi-cuenta?ok", request, response);
    }
}
