package com.sixt.frontend.controlador;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;

// Acceso al usuario que inicio sesion (lo que devolvio el backend en el login).
// FiltroLogin garantiza que en las pantallas protegidas siempre hay uno.
final class UsuarioEnSesion {

    private UsuarioEnSesion() {
    }

    static Object id(HttpServletRequest request) {
        return usuario(request).get("id");
    }

    // Unico lugar donde se pregunta por un permiso ("VEHICULO:VER"). Son los efectivos
    // que devolvio el backend; la decision real la toma siempre el backend.
    static boolean puede(HttpServletRequest request, String permiso) {
        return puede(usuario(request), permiso);
    }

    static boolean puede(Map<?, ?> usuario, String permiso) {
        return usuario.get("permisos") instanceof Collection<?> permisos && permisos.contains(permiso);
    }

    private static Map<?, ?> usuario(HttpServletRequest request) {
        return (Map<?, ?>) request.getSession().getAttribute("usuario");
    }
}
