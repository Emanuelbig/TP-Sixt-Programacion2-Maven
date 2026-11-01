package com.sixt.frontend.controlador;

import com.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/login", "/logout"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getServletPath().equals("/logout")) {
            try {
                ClienteBackend.post(request, "/api/logout", java.util.Map.of());
            } catch (ServletException e) {
                log("No se pudo cerrar la sesion remota; se cierra la sesion local", e);
            } finally {
                request.getSession().invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // El backend decide si el email y la contraseña son validos
        ClienteBackend.Respuesta respuesta = ClienteBackend.post(request, "/api/login",
                Formulario.campos(request, "email", "contrasenia"));

        if (respuesta.ok()) {
            // Sesion nueva al entrar, para que nadie pueda reutilizar un id de sesion anterior.
            // Se guarda lo que devolvio el backend: id, nombre, email, rol y permisos.
            HttpSession anterior = request.getSession(false);
            if (anterior != null) {
                anterior.invalidate();
            }
            HttpSession nueva = request.getSession(true);
            nueva.setAttribute("usuario", respuesta.cuerpo());
            nueva.setAttribute("cookieBackend", respuesta.cookieSesion());
            response.sendRedirect(request.getContextPath() + "/inicio");
        } else {
            request.setAttribute("error", respuesta.error());
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
        }
    }
}
