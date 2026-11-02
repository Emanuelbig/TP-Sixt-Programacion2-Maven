package com.sixt.frontend.controlador;

import java.util.Map;

// Que permisos (efectivos, con la cascada) habilitan cada pantalla: alcanza con tener uno.
// Para proteger una pantalla nueva alcanza con agregarla al mapa.
// Una ruta que no esta en el mapa solo pide haber iniciado sesion (inicio, mi cuenta).
final class PermisosDePantalla {

    private static final Map<String, String[]> PERMISOS_POR_PANTALLA = Map.ofEntries(
            Map.entry("/oficinas", permisos("OFICINA:VER")),
            Map.entry("/oficinas/baja", permisos("OFICINA:ELIMINAR")),
            Map.entry("/vehiculos", permisos("VEHICULO:VER")),
            Map.entry("/vehiculos/baja", permisos("VEHICULO:ELIMINAR")),
            // Modelos y tipos son el catalogo de la flota: parte del recurso VEHICULO
            Map.entry("/modelos", permisos("VEHICULO:VER")),
            Map.entry("/modelos/baja", permisos("VEHICULO:ELIMINAR")),
            Map.entry("/tipos", permisos("VEHICULO:VER")),
            Map.entry("/tipos/baja", permisos("VEHICULO:ELIMINAR")),
            // Una sola pantalla para empleados y clientes: el admin ve a todos, el vendedor solo
            // a los clientes. Que persona puede tocar cada uno lo decide el backend fila por fila
            Map.entry("/personas", permisos("USUARIO:VER", "CLIENTE:VER")),
            Map.entry("/personas/baja", permisos("USUARIO:ELIMINAR", "CLIENTE:ELIMINAR")),
            Map.entry("/roles", permisos("ROL:VER")),
            Map.entry("/roles/baja", permisos("ROL:ELIMINAR")),
            Map.entry("/reservas", permisos("RESERVA:VER")),
            Map.entry("/reservas/devolver", permisos("RESERVA:EDITAR")),
            Map.entry("/reservas/cancelar", permisos("RESERVA:EDITAR")),
            Map.entry("/mis-reservas", permisos("RESERVA:ALQUILAR")),
            Map.entry("/mis-reservas/cancelar", permisos("RESERVA:ALQUILAR")),
            Map.entry("/reservar", permisos("RESERVA:ALQUILAR")),
            Map.entry("/reportes", permisos("REPORTE:VER")));

    private PermisosDePantalla() {
    }

    private static String[] permisos(String... codigos) {
        return codigos;
    }

    // null si la pantalla no pide un permiso en particular
    static String[] permisosPara(String ruta) {
        return PERMISOS_POR_PANTALLA.get(ruta);
    }

    static boolean puedeEntrar(Map<?, ?> usuario, String ruta) {
        String[] permisos = permisosPara(ruta);
        if (permisos == null) {
            return true;
        }
        for (String permiso : permisos) {
            if (UsuarioEnSesion.puede(usuario, permiso)) {
                return true;
            }
        }
        return false;
    }
}
