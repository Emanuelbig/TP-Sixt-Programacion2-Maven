package com.sixt.configuracion;

import com.sixt.dto.UsuarioLogueado;
import com.sixt.excepcion.AccesoDenegadoExcepcion;
import com.sixt.excepcion.CredencialesInvalidasExcepcion;
import com.sixt.modelo.Permiso;
import com.sixt.servicio.AutenticacionServicio;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AutorizacionApi extends OncePerRequestFilter {
    private final AutenticacionServicio autenticacion;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        // El backend sirve una API: solo el login es publico. Las variantes
        // codificadas de una ruta tampoco pueden eludir la autenticacion.
        return ruta.equals("/api/login") && request.getMethod().equals("POST");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            autorizar(request);
        } catch (CredencialesInvalidasExcepcion e) {
            error(response, 401, "La sesion no es valida o ha expirado");
            return;
        } catch (AccesoDenegadoExcepcion e) {
            error(response, 403, "No tenes permiso para realizar esta operacion");
            return;
        }
        chain.doFilter(request, response);
    }

    private void error(HttpServletResponse response, int estado, String mensaje) throws IOException {
        response.setStatus(estado);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\":\"" + mensaje + "\"}");
    }

    private void autorizar(HttpServletRequest request) {
        var sesion = request.getSession(false);
        if (sesion == null || !(sesion.getAttribute("usuarioId") instanceof Long id)) {
            throw new CredencialesInvalidasExcepcion();
        }
        UsuarioLogueado usuario = autenticacion.usuarioDeSesion(id);
        request.setAttribute("usuarioAutenticado", usuario);
        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        String metodo = request.getMethod();
        boolean lectura = "GET".equals(metodo);
        if ((ruta.equals("/api/sesion") && lectura) || (ruta.equals("/api/logout") && "POST".equals(metodo))
                || (ruta.equals("/api/resumen") && lectura)) return;
        if (ruta.matches("/api/personas/\\d+/contrasenia") && "PATCH".equals(metodo)) {
            if (!ruta.equals("/api/personas/" + usuario.id() + "/contrasenia")) throw new AccesoDenegadoExcepcion();
            return;
        }
        if (ruta.equals("/api/clientes") || ruta.startsWith("/api/clientes/")) {
            exigir(usuario, Permiso.ADMINISTRAR_CLIENTES);
        } else if (ruta.equals("/api/reservas") || ruta.startsWith("/api/reservas/")) {
            if (lectura && !usuario.permisos().contains(Permiso.VENDER)) {
                exigir(usuario, Permiso.ALQUILAR);
                // El controlador limita la consulta a la identidad autenticada.
            } else exigir(usuario, Permiso.VENDER);
        } else if (lectura && (ruta.equals("/api/vehiculos") || ruta.equals("/api/modelos") || ruta.equals("/api/oficinas"))) {
            if (!usuario.permisos().contains(Permiso.ADMINISTRAR)) exigir(usuario, Permiso.VENDER);
        } else {
            exigir(usuario, Permiso.ADMINISTRAR);
        }
        return;
    }

    private void exigir(UsuarioLogueado usuario, Permiso permiso) {
        if (!usuario.permisos().contains(permiso)) throw new AccesoDenegadoExcepcion();
    }
}
