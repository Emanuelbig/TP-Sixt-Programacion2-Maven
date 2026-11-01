package com.sixt.frontend.controlador;

import com.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

// Pantalla del vendedor: todas las reservas + reservar para cualquier cliente
@WebServlet(urlPatterns = {"/reservas", "/reservas/devolver", "/reservas/cancelar"})
public class ReservasServlet extends PantallaServlet {

    @Override
    protected void cargarDatos(HttpServletRequest request) throws ServletException {
        if (request.getParameter("ok") != null) {
            request.setAttribute("mensaje", "La reserva se creó correctamente");
        }
        request.setAttribute("reservas", ClienteBackend.getLista(request, "/api/reservas"));
        request.setAttribute("personas", ClienteBackend.getLista(request, "/api/clientes"));
        BuscadorReserva.cargar(request);
    }

    @Override
    protected String vista() {
        return "reservas.jsp";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Devolver o cancelar: misma forma, cambia la accion al final de la URL
        String ruta = request.getServletPath();
        if (ruta.equals("/reservas/devolver") || ruta.equals("/reservas/cancelar")) {
            String accion = ruta.substring("/reservas/".length());
            ClienteBackend.Respuesta respuesta = ClienteBackend.post(request,
                    "/api/reservas/" + request.getParameter("id") + "/" + accion, Map.of());
            responder(respuesta, "/reservas", request, response);
            return;
        }
        Map<String, Object> datos = BuscadorReserva.datos(request);
        datos.put("clienteId", request.getParameter("clienteId"));
        // El vendedor es siempre quien inicio sesion: no viaja en el formulario, asi no se puede falsificar
        datos.put("vendedorId", UsuarioEnSesion.id(request));
        responder(ClienteBackend.post(request, "/api/reservas", datos), "/reservas?ok", request, response);
    }
}
