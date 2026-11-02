package com.sixt.seguridad.configuracion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

// Los rechazos de Spring Security con el mismo JSON que el resto de la API: {"error": "..."}
//  - 401: sin token, token invalido o vencido, usuario dado de baja o sesion cerrada
//  - 403: el rol no tiene el permiso que pide la URL
class RespuestasDeSeguridad implements AuthenticationEntryPoint, AccessDeniedHandler {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException e) throws IOException {
        escribir(response, HttpServletResponse.SC_UNAUTHORIZED, "La sesion no es valida o ha expirado");
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException e) throws IOException {
        escribir(response, HttpServletResponse.SC_FORBIDDEN, "No tenes permiso para realizar esta operacion");
    }

    private static void escribir(HttpServletResponse response, int estado, String mensaje) throws IOException {
        response.setStatus(estado);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\":\"" + mensaje + "\"}");
    }
}
