package ar.edu.utn.inspt.sixt.frontend.controlador;

import ar.edu.utn.inspt.sixt.frontend.BackendClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/personas")
public class PersonasServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("personas", BackendClient.getLista("/api/personas"));
        request.setAttribute("roles", BackendClient.getLista("/api/roles"));
        request.getRequestDispatcher("/WEB-INF/jsp/personas.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BackendClient.Respuesta respuesta = BackendClient.post("/api/personas", Map.of(
                "name", request.getParameter("name"),
                "dni", request.getParameter("dni"),
                "email", request.getParameter("email"),
                "phone", request.getParameter("phone"),
                "roleId", request.getParameter("roleId")));

        if (!respuesta.ok()) {
            request.setAttribute("error", respuesta.error());
            doGet(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/personas");
    }
}
