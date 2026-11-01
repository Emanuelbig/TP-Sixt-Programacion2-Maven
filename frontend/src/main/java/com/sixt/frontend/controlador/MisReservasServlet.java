package com.sixt.frontend.controlador;

import com.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

// Las reservas del cliente que inicio sesion
@WebServlet("/mis-reservas")
public class MisReservasServlet extends PantallaServlet {

    @Override
    protected void cargarDatos(HttpServletRequest request) throws ServletException {
        if (request.getParameter("ok") != null) {
            request.setAttribute("mensaje", "¡Listo! Tu reserva quedó confirmada");
        }
        Object id = UsuarioEnSesion.id(request);
        request.setAttribute("reservas", ClienteBackend.getLista(request, "/api/reservas?clienteId=" + id));
    }

    @Override
    protected String vista() {
        return "mis-reservas.jsp";
    }
}
