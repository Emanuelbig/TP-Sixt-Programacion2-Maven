package ar.edu.utn.inspt.sixt.frontend.controlador;

import ar.edu.utn.inspt.sixt.frontend.BackendClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {"/reservas", "/reservas/devolver"})
public class ReservasServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("reservas", BackendClient.getLista("/api/reservas"));
        request.setAttribute("personas", BackendClient.getLista("/api/personas"));
        request.setAttribute("vehiculos", BackendClient.getLista("/api/vehiculos"));
        request.setAttribute("oficinas", BackendClient.getLista("/api/oficinas"));
        request.getRequestDispatcher("/WEB-INF/jsp/reservas.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BackendClient.Respuesta respuesta;

        if (request.getServletPath().equals("/reservas/devolver")) {
            respuesta = BackendClient.post("/api/reservas/" + request.getParameter("id") + "/devolver", Map.of());
        } else {
            respuesta = BackendClient.post("/api/reservas", Map.of(
                    "clientId", request.getParameter("clientId"),
                    "salesmanId", request.getParameter("salesmanId"),
                    "vehicleId", request.getParameter("vehicleId"),
                    "originOfficeId", request.getParameter("originOfficeId"),
                    "destinationOfficeId", request.getParameter("destinationOfficeId"),
                    "dateFrom", request.getParameter("dateFrom"),
                    "dateTo", request.getParameter("dateTo"),
                    "gasLiters", request.getParameter("gasLiters")));
        }

        if (!respuesta.ok()) {
            request.setAttribute("error", respuesta.error());
            doGet(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/reservas");
    }
}
