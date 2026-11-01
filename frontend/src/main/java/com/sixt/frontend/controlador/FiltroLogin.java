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
import java.util.Collection;
import java.util.Map;

// Controla el acceso a cada pantalla:
//  - sin sesion -> al login
//  - con sesion pero sin el permiso que pide la pantalla -> 403
// Para proteger una pantalla nueva alcanza con agregarla al mapa.
@WebFilter("/*")
public class FiltroLogin extends HttpFilter {

    private static final Map<String, String> PERMISO_POR_PANTALLA = Map.ofEntries(
            Map.entry("/oficinas", "ADMINISTRAR"),
            Map.entry("/oficinas/baja", "ADMINISTRAR"),
            Map.entry("/vehiculos", "ADMINISTRAR"),
            Map.entry("/vehiculos/baja", "ADMINISTRAR"),
            Map.entry("/modelos", "ADMINISTRAR"),
            Map.entry("/modelos/baja", "ADMINISTRAR"),
            Map.entry("/tipos", "ADMINISTRAR"),
            Map.entry("/tipos/baja", "ADMINISTRAR"),
            Map.entry("/personas", "ADMINISTRAR"),
            Map.entry("/personas/baja", "ADMINISTRAR"),
            Map.entry("/roles", "ADMINISTRAR"),
            Map.entry("/roles/baja", "ADMINISTRAR"),
            Map.entry("/clientes", "ADMINISTRAR_CLIENTES"),
            Map.entry("/clientes/baja", "ADMINISTRAR_CLIENTES"),
            Map.entry("/reservas", "VENDER"),
            Map.entry("/reservas/devolver", "VENDER"),
            Map.entry("/reservas/cancelar", "VENDER"),
            Map.entry("/mis-reservas", "ALQUILAR"),
            Map.entry("/mis-reservas/cancelar", "ALQUILAR"),
            Map.entry("/reservar", "ALQUILAR"));

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String ruta = request.getServletPath();
        // El login, los estilos y el icono se ven sin sesion (si no, la pantalla de login quedaria sin CSS)
        if (ruta.equals("/login") || ruta.startsWith("/css/") || ruta.equals("/favicon.svg")) {
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

        String permisoNecesario = PERMISO_POR_PANTALLA.get(ruta);
        if (permisoNecesario != null && !tienePermiso(usuario, permisoNecesario)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "No tenés permiso para entrar a esta pantalla");
            return;
        }
        chain.doFilter(request, response);
    }

    private static boolean tienePermiso(Map<?, ?> usuario, String permiso) {
        return usuario.get("permisos") instanceof Collection<?> permisos && permisos.contains(permiso);
    }
}
