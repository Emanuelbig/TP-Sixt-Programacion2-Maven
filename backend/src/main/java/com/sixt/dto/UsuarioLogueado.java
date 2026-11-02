package com.sixt.dto;

import com.sixt.modelo.Persona;
import java.util.Set;

// Lo que el frontend necesita saber de quien inicio sesion (nunca la contraseña).
// Los permisos son los efectivos (con la cascada), como texto: "VEHICULO:VER"
public record UsuarioLogueado(Long id, String nombre, String direccion, String email, String rol, Set<String> permisos) {

    public static UsuarioLogueado de(Persona persona) {
        return new UsuarioLogueado(persona.getId(), persona.getNombre(), persona.getDireccion(), persona.getEmail(),
                persona.getRol().getNombre(), persona.getRol().codigosDePermisosEfectivos());
    }
}
