package com.sixt.frontend.controlador;

import com.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {"/login", "/logout"})
public class LoginServlet extends HttpServlet {

    // Salir, o simplemente volver a la pantalla de login (por ejemplo con "atras"), cierra la
    // sesion que hubiera abierta. Asi nadie puede ir "adelante" en una computadora compartida
    // y seguir usando la sesion de otro: siempre hay que volver a poner las credenciales.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cerrarSesion(request);
        if (request.getServletPath().equals("/logout")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }

    // Invalida el token en el backend (deja de servir aunque alguien lo haya copiado)
    // y despues la sesion de Tomcat, con su cookie JSESSIONID
    private void cerrarSesion(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        if (sesion == null) {
            return;
        }
        if (sesion.getAttribute("tokenBackend") != null) {
            try {
                ClienteBackend.post(request, "/api/logout", Map.of());
            } catch (ServletException e) {
                log("No se pudo cerrar la sesion remota; se cierra la sesion local", e);
            }
        }
        sesion.invalidate();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // El backend decide si el email y la contraseña son validos
        ClienteBackend.Respuesta respuesta = ClienteBackend.post(request, "/api/login",
                Formulario.campos(request, "email", "contrasenia"));

        if (respuesta.ok()) {
            // Sesion nueva al entrar, para que nadie pueda reutilizar un id de sesion anterior.
            // Se guarda el token y despues los datos de la persona (nombre, rol, permisos).
            HttpSession anterior = request.getSession(false);
            if (anterior != null) {
                anterior.invalidate();
            }
            HttpSession nueva = request.getSession(true);
            nueva.setAttribute("tokenBackend", respuesta.cuerpo().get("token"));
            ClienteBackend.Respuesta sesion = ClienteBackend.consultarSesion(request);
            if (!sesion.ok()) {
                throw new ServletException(sesion.error());
            }
            nueva.setAttribute("usuario", sesion.cuerpo());
            response.sendRedirect(request.getContextPath() + "/inicio");
        } else {
            request.setAttribute("error", respuesta.error());
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
        }
    }
}
