package ar.edu.utn.inspt.sixt.frontend.controlador;

import ar.edu.utn.inspt.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

// Las reservas del cliente que inicio sesion
@WebServlet("/mis-reservas")
public class MisReservasServlet extends PantallaServlet {

    @Override
    protected void cargarDatos(HttpServletRequest request) throws ServletException {
        Object id = UsuarioEnSesion.id(request);
        request.setAttribute("reservas", ClienteBackend.getLista("/api/reservas?clienteId=" + id));
    }

    @Override
    protected String vista() {
        return "mis-reservas.jsp";
    }
}
