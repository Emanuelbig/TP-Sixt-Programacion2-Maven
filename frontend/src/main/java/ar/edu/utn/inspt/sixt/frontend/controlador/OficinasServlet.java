package ar.edu.utn.inspt.sixt.frontend.controlador;

import ar.edu.utn.inspt.sixt.frontend.BackendClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/oficinas")
public class OficinasServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("oficinas", BackendClient.getLista("/api/oficinas"));
        request.getRequestDispatcher("/WEB-INF/jsp/oficinas.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BackendClient.Respuesta respuesta = BackendClient.post("/api/oficinas", Map.of(
                "zone", request.getParameter("zone"),
                "address", request.getParameter("address")));

        if (!respuesta.ok()) {
            request.setAttribute("error", respuesta.error());
            doGet(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/oficinas");
    }
}
