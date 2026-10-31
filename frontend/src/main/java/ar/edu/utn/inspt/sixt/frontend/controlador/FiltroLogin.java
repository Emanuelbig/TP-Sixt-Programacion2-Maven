package ar.edu.utn.inspt.sixt.frontend.controlador;

import jakarta.servlet.FilterChain;
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

    private static final Map<String, String> PERMISO_POR_PANTALLA = Map.of(
            "/oficinas", "ADMINISTRAR",
            "/vehiculos", "ADMINISTRAR",
            "/vehiculos/baja", "ADMINISTRAR",
            "/personas", "ADMINISTRAR",
            "/personas/baja", "ADMINISTRAR",
            "/roles", "ADMINISTRAR",
            "/reservas", "VENDER",
            "/reservas/devolver", "VENDER",
            "/mis-reservas", "ALQUILAR");

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String ruta = request.getServletPath();
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
