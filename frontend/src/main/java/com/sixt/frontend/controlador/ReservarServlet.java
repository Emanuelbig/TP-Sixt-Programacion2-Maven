package com.sixt.frontend.controlador;

import com.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

// Pantalla del cliente: busca autos libres y se hace una reserva a su nombre (reserva online, sin vendedor)
@WebServlet("/reservar")
public class ReservarServlet extends PantallaServlet {

    @Override
    protected void cargarDatos(HttpServletRequest request) throws ServletException {
        BuscadorReserva.cargar(request);
    }

    @Override
    protected String vista() {
        return "reservar.jsp";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, Object> datos = BuscadorReserva.datos(request);
        // El cliente es siempre quien inicio sesion: no puede reservar a nombre de otro
        datos.put("clienteId", UsuarioEnSesion.id(request));
        responder(ClienteBackend.post(request, "/api/reservas", datos), "/mis-reservas?ok", request, response);
    }
}
