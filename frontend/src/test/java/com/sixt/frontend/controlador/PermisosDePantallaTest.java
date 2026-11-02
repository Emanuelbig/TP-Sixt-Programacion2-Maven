package com.sixt.frontend.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

// Que puede ver cada usuario de prueba, con los permisos efectivos que devuelve el backend
// en /api/sesion para los roles iniciales (ADMIN, VENDEDOR, CLIENTE).
class PermisosDePantallaTest {

    private static final Map<String, Object> ADMIN = usuario(
            "OFICINA:VER", "OFICINA:CREAR", "OFICINA:EDITAR", "OFICINA:ELIMINAR", "OFICINA:ADMINISTRAR",
            "REPORTE:VER",
            "VEHICULO:VER", "VEHICULO:CREAR", "VEHICULO:EDITAR", "VEHICULO:ELIMINAR", "VEHICULO:ADMINISTRAR",
            "CLIENTE:VER", "CLIENTE:CREAR", "CLIENTE:EDITAR", "CLIENTE:ELIMINAR", "CLIENTE:ADMINISTRAR",
            "USUARIO:VER", "USUARIO:CREAR", "USUARIO:EDITAR", "USUARIO:ELIMINAR", "USUARIO:ADMINISTRAR",
            "RESERVA:VER_PROPIAS", "RESERVA:VER", "RESERVA:CREAR", "RESERVA:EDITAR", "RESERVA:ADMINISTRAR",
            "RESERVA:VENDER", "ROL:VER");
    private static final Map<String, Object> VENDEDOR = usuario(
            "RESERVA:VER_PROPIAS", "RESERVA:VER", "RESERVA:CREAR", "RESERVA:EDITAR", "RESERVA:ADMINISTRAR",
            "RESERVA:VENDER", "CLIENTE:VER", "CLIENTE:CREAR", "CLIENTE:EDITAR", "CLIENTE:ELIMINAR",
            "CLIENTE:ADMINISTRAR", "VEHICULO:VER", "OFICINA:VER");
    private static final Map<String, Object> CLIENTE = usuario("RESERVA:VER_PROPIAS", "RESERVA:ALQUILAR");

    private static Map<String, Object> usuario(String... permisos) {
        return Map.of("id", 1, "permisos", List.of(permisos));
    }

    private static Map<String, Object> segunRol(String rol) {
        if (rol.equals("ADMIN")) {
            return ADMIN;
        }
        if (rol.equals("VENDEDOR")) {
            return VENDEDOR;
        }
        return CLIENTE;
    }

    @ParameterizedTest(name = "{0} en {1}: {2}")
    @CsvSource({
            "ADMIN,    /oficinas,          true",
            "ADMIN,    /vehiculos,         true",
            "ADMIN,    /modelos,           true",
            "ADMIN,    /personas,          true",
            "ADMIN,    /roles,             true",
            "ADMIN,    /roles/baja,        false",
            "ADMIN,    /personas/baja,     true",
            "ADMIN,    /reservas,          true",
            "ADMIN,    /mis-reservas,      false",
            "ADMIN,    /oficinas/baja,     true",
            "ADMIN,    /modelos/baja,      true",
            "ADMIN,    /reportes,          true",
            "ADMIN,    /reservas/cancelar, true",
            "VENDEDOR, /reservas,          true",
            "VENDEDOR, /reservas/devolver, true",
            "VENDEDOR, /personas/baja,     true",
            "VENDEDOR, /vehiculos,         true",
            "VENDEDOR, /vehiculos/baja,    false",
            "VENDEDOR, /personas,          true",
            "VENDEDOR, /roles,             false",
            "VENDEDOR, /reservar,          false",
            "VENDEDOR, /oficinas/baja,     false",
            "VENDEDOR, /tipos/baja,        false",
            "VENDEDOR, /reportes,          false",
            "VENDEDOR, /reservas/cancelar, true",
            "CLIENTE,  /reservar,          true",
            "CLIENTE,  /mis-reservas,      true",
            "CLIENTE,  /mis-reservas/cancelar, true",
            "CLIENTE,  /reservas/cancelar, false",
            "CLIENTE,  /reportes,          false",
            "CLIENTE,  /reservas,          false",
            "CLIENTE,  /personas,          false",
            "CLIENTE,  /personas/baja,     false",
            "CLIENTE,  /vehiculos,         false",
            "CLIENTE,  /oficinas,          false",
            "CLIENTE,  /inicio,            true",
            "CLIENTE,  /mi-cuenta,         true"
    })
    void cadaRolEntraSoloASusPantallas(String rol, String ruta, boolean esperado) {
        assertEquals(esperado, PermisosDePantalla.puedeEntrar(segunRol(rol), ruta));
    }

    @Test
    void lasPantallasSinPermisoParticularSoloPidenSesion() {
        assertNull(PermisosDePantalla.permisosPara("/inicio"));
        assertNull(PermisosDePantalla.permisosPara("/mi-cuenta"));
        assertEquals("RESERVA:ALQUILAR", PermisosDePantalla.permisosPara("/reservar")[0]);
        // La pantalla Personas se habilita con cualquiera de los dos permisos
        assertEquals(2, PermisosDePantalla.permisosPara("/personas").length);
    }

    @Test
    void unUsuarioSinListaDePermisosNoPuedeNada() {
        Map<String, Object> sinPermisos = Map.of("id", 1);
        assertFalse(UsuarioEnSesion.puede(sinPermisos, "OFICINA:VER"));
        assertFalse(PermisosDePantalla.puedeEntrar(sinPermisos, "/oficinas"));
        assertTrue(PermisosDePantalla.puedeEntrar(sinPermisos, "/inicio"));
    }

    @Test
    void elPermisoTieneQueCoincidirExacto() {
        // Tener VER no habilita la pantalla de baja, ni un permiso parecido de otro recurso
        assertFalse(UsuarioEnSesion.puede(VENDEDOR, "VEHICULO:ELIMINAR"));
        assertFalse(UsuarioEnSesion.puede(VENDEDOR, "USUARIO:VER"));
        assertTrue(UsuarioEnSesion.puede(VENDEDOR, "VEHICULO:VER"));
    }
}
