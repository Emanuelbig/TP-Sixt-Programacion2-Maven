package com.sixt.frontend.controlador;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

// Acceso al usuario que inicio sesion (lo que devolvio el backend en el login).
// FiltroLogin garantiza que en las pantallas protegidas siempre hay uno.
final class UsuarioEnSesion {

    private UsuarioEnSesion() {
    }

    static Object id(HttpServletRequest request) {
        Map<?, ?> usuario = (Map<?, ?>) request.getSession().getAttribute("usuario");
        return usuario.get("id");
    }
}
