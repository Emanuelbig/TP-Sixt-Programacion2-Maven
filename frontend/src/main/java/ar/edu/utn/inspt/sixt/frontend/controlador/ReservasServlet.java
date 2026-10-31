package ar.edu.utn.inspt.sixt.frontend.controlador;

import ar.edu.utn.inspt.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {"/reservas", "/reservas/devolver"})
public class ReservasServlet extends PantallaServlet {

    @Override
    protected void cargarDatos(HttpServletRequest request) throws ServletException {
        request.setAttribute("reservas", ClienteBackend.getLista("/api/reservas"));
        request.setAttribute("personas", ClienteBackend.getLista("/api/personas"));
        request.setAttribute("vehiculos", ClienteBackend.getLista("/api/vehiculos"));
        request.setAttribute("oficinas", ClienteBackend.getLista("/api/oficinas"));
    }

    @Override
    protected String vista() {
        return "reservas.jsp";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ClienteBackend.Respuesta respuesta;

        if (request.getServletPath().equals("/reservas/devolver")) {
            respuesta = ClienteBackend.post("/api/reservas/" + request.getParameter("id") + "/devolver", Map.of());
        } else {
            respuesta = ClienteBackend.post("/api/reservas", Formulario.campos(request,
                    "clienteId", "vendedorId", "vehiculoId",
                    "oficinaOrigenId", "oficinaDestinoId",
                    "fechaDesde", "fechaHasta", "litrosNafta"));
        }
        responder(respuesta, "/reservas", request, response);
    }
}
