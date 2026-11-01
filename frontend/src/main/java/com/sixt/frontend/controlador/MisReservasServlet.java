package com.sixt.frontend.controlador;

import com.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

// Las reservas del cliente que inicio sesion
@WebServlet(urlPatterns = {"/mis-reservas", "/mis-reservas/cancelar"})
public class MisReservasServlet extends PantallaServlet {

    @Override
    protected void cargarDatos(HttpServletRequest request) throws ServletException {
        if (request.getParameter("ok") != null) {
            request.setAttribute("mensaje", "¡Listo! Tu reserva quedó confirmada");
        }
        Object id = UsuarioEnSesion.id(request);
        request.setAttribute("reservas", ClienteBackend.getLista(request, "/api/reservas?clienteId=" + id));
        // Para mostrar "Cancelar" solo en las que todavia no empezaron
        request.setAttribute("hoy", LocalDate.now().toString());
    }

    @Override
    protected String vista() {
        return "mis-reservas.jsp";
    }

    // El cliente cancela una reserva suya. Que sea suya lo controla el backend
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ClienteBackend.Respuesta respuesta = ClienteBackend.post(request,
                "/api/reservas/" + request.getParameter("id") + "/cancelar", Map.of());
        responder(respuesta, "/mis-reservas", request, response);
    }
}
