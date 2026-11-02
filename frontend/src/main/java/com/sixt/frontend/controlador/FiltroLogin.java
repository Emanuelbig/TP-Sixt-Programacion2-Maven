package com.sixt.frontend.controlador;

import jakarta.servlet.FilterChain;
import com.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

// Controla el acceso a cada pantalla:
//  - sin sesion -> al login
//  - con sesion pero sin el permiso que pide la pantalla -> 403
//  - ninguna pantalla queda guardada en el navegador: si no, con "atras" y "adelante"
//    despues de salir se veria una copia vieja sin pasar por este filtro
// Que permiso pide cada pantalla lo decide PermisosDePantalla.
@WebFilter("/*")
public class FiltroLogin extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String ruta = request.getServletPath();
        // Los estilos y el icono se ven sin sesion y se pueden guardar en cache
        if (ruta.startsWith("/css/") || ruta.equals("/favicon.svg")) {
            chain.doFilter(request, response);
            return;
        }
        prohibirCache(response);
        // El login se ve sin sesion (es donde se inicia)
        if (ruta.equals("/login")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession sesion = request.getSession(false);
        Map<?, ?> usuario = sesion == null ? null : (Map<?, ?>) sesion.getAttribute("usuario");
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (!ruta.equals("/logout")) {
            ClienteBackend.Respuesta estado = ClienteBackend.consultarSesion(request);
            if (estado.estado() == 401) {
                sesion.invalidate();
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            if (!estado.ok()) throw new ServletException(estado.error());
            usuario = estado.cuerpo();
            sesion.setAttribute("usuario", usuario);
        }

        if (!PermisosDePantalla.puedeEntrar(usuario, ruta)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "No tenés permiso para entrar a esta pantalla");
            return;
        }
        chain.doFilter(request, response);
    }

    // no-store: el navegador no guarda la pagina (ni en el historial de atras/adelante),
    // asi cada vez que se vuelve a ella la pide al servidor y pasa por este filtro
    private static void prohibirCache(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}
