package ar.edu.utn.inspt.sixt.dto;

import ar.edu.utn.inspt.sixt.modelo.Permiso;
import ar.edu.utn.inspt.sixt.modelo.Persona;
import java.util.Set;

// Lo que el frontend necesita saber de quien inicio sesion (nunca la contraseña)
public record UsuarioLogueado(Long id, String nombre, String email, String rol, Set<Permiso> permisos) {

    public static UsuarioLogueado de(Persona persona) {
        return new UsuarioLogueado(persona.getId(), persona.getNombre(), persona.getEmail(),
                persona.getRol().getNombre(), Set.copyOf(persona.getRol().getPermisos()));
    }
}
