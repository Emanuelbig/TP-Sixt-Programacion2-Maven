package com.sixt.seguridad.servicio;

import com.sixt.excepcion.AccesoDenegadoExcepcion;
import com.sixt.excepcion.ConflictoExcepcion;
import com.sixt.seguridad.modelo.Accion;
import com.sixt.seguridad.modelo.Permiso;
import com.sixt.seguridad.modelo.Recurso;
import com.sixt.seguridad.modelo.Rol;
import com.sixt.seguridad.modelo.Usuario;
import com.sixt.seguridad.repositorio.UsuarioRepositorio;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// Reglas contra la escalada de privilegios, en un solo lugar (responsabilidad unica):
//  - nadie otorga un rol con permisos que el mismo no tiene;
//  - nadie modifica a alguien con permisos que el no tiene;
//  - nadie cambia su propio rol, ni lo edita, ni se da de baja a si mismo;
//  - siempre queda al menos un usuario activo con ROL:ADMINISTRAR.
// Las usan UsuarioServicio y RolServicio, que se ocupan solo del ABM.
@Component
@RequiredArgsConstructor
public class ControlDePrivilegios {

    private static final Permiso ADMINISTRA_ROLES = Permiso.de(Recurso.ROL, Accion.ADMINISTRAR);

    private final UsuarioActual usuarioActual;
    private final UsuarioRepositorio usuarioRepositorio;

    // Las capacidades (ALQUILAR, VENDER) no cuentan: dicen que papel cumple alguien en una reserva
    public void exigirPuedeOtorgar(Rol rol) {
        if (!usuarioActual.puedeTodos(sinCapacidades(rol.getPermisosEfectivos()))) {
            throw new AccesoDenegadoExcepcion("No podes otorgar permisos que no tenes (rol " + rol.getNombre() + ")");
        }
    }

    public void exigirPuedeModificar(Usuario usuario) {
        if (!usuarioActual.puedeTodos(sinCapacidades(usuario.getRol().getPermisosEfectivos()))) {
            throw new AccesoDenegadoExcepcion("No podes modificar a alguien con permisos que no tenes");
        }
    }

    public void exigirQueNoSeaElMismo(Long usuarioId, String mensaje) {
        if (usuarioActual.esEl(usuarioId)) {
            throw new AccesoDenegadoExcepcion(mensaje);
        }
    }

    public void exigirQueNoSeaSuPropioRol(Rol rol) {
        if (!usuarioActual.estaIdentificado()) {
            return;
        }
        Usuario propio = usuarioRepositorio.findById(usuarioActual.id()).orElse(null);
        if (propio != null && propio.getRol().getId().equals(rol.getId())) {
            throw new AccesoDenegadoExcepcion("No podes modificar tu propio rol");
        }
    }

    // Se verifica despues de aplicar el cambio: si falla, la excepcion deshace toda la transaccion
    public void verificarQueQuedaUnAdministradorDeRoles() {
        for (Usuario usuario : usuarioRepositorio.findByActivoTrue()) {
            if (usuario.getRol().tiene(ADMINISTRA_ROLES)) {
                return;
            }
        }
        throw new ConflictoExcepcion("Tiene que quedar al menos un usuario activo con " + ADMINISTRA_ROLES);
    }

    private static Set<Permiso> sinCapacidades(Set<Permiso> permisos) {
        Set<Permiso> resultado = new HashSet<>();
        for (Permiso permiso : permisos) {
            if (!permiso.esCapacidad()) {
                resultado.add(permiso);
            }
        }
        return resultado;
    }
}
