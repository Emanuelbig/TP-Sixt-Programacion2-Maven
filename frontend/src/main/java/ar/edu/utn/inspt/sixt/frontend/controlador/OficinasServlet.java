package ar.edu.utn.inspt.sixt.frontend.controlador;

import ar.edu.utn.inspt.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/oficinas")
public class OficinasServlet extends PantallaServlet {

    @Override
    protected void cargarDatos(HttpServletRequest request) throws ServletException {
        List<Map<String, Object>> oficinas = ClienteBackend.getLista("/api/oficinas");
        request.setAttribute("oficinas", oficinas);
        marcarEdicion(request, oficinas);
    }

    @Override
    protected String vista() {
        return "oficinas.jsp";
    }

    // Con id es una edicion (PUT), sin id es un alta (POST)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, Object> datos = Formulario.campos(request, "zona", "direccion");
        ClienteBackend.Respuesta respuesta = esEdicion(request)
                ? ClienteBackend.put("/api/oficinas/" + request.getParameter("id"), datos)
                : ClienteBackend.post("/api/oficinas", datos);
        responder(respuesta, "/oficinas", request, response);
    }
}
