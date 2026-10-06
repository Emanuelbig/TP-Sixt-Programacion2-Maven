package ar.edu.utn.inspt.sixt.frontend.controlador;

import ar.edu.utn.inspt.sixt.frontend.BackendClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/vehiculos")
public class VehiculosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("vehiculos", BackendClient.getLista("/api/vehiculos"));
        request.setAttribute("modelos", BackendClient.getLista("/api/modelos"));
        request.setAttribute("oficinas", BackendClient.getLista("/api/oficinas"));
        request.getRequestDispatcher("/WEB-INF/jsp/vehiculos.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BackendClient.Respuesta respuesta = BackendClient.post("/api/vehiculos", Map.of(
                "plate", request.getParameter("plate"),
                "colour", request.getParameter("colour"),
                "year", request.getParameter("year"),
                "modelId", request.getParameter("modelId"),
                "officeId", request.getParameter("officeId")));

        if (!respuesta.ok()) {
            request.setAttribute("error", respuesta.error());
            doGet(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/vehiculos");
    }
}
