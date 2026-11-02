package com.sixt.seguridad.servicio;

import com.sixt.excepcion.AccesoDenegadoExcepcion;
import com.sixt.seguridad.modelo.Accion;
import com.sixt.seguridad.modelo.Permiso;
import com.sixt.seguridad.modelo.Recurso;
import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

// Quien hace el pedido actual y que puede hacer, segun lo que cargo ConvertidorDeToken.
// Sin autenticacion en el contexto es un proceso interno (carga inicial o un test de
// integracion) y no se restringe: todo pedido HTTP pasa antes por los filtros de Spring
// Security, que siempre dejan una autenticacion (aunque sea anonima).
@Component
public class UsuarioActual {

    public boolean esProcesoInterno() {
        return autenticacion() == null;
    }

    public boolean estaIdentificado() {
        return autenticacion() instanceof JwtAuthenticationToken;
    }

    // El id viaja como "subject" del token
    public Long id() {
        if (!(autenticacion() instanceof JwtAuthenticationToken token)) {
            throw new AccesoDenegadoExcepcion();
        }
        return Long.valueOf(token.getName());
    }

    public boolean esEl(Long usuarioId) {
        return estaIdentificado() && id().equals(usuarioId);
    }

    public boolean puede(Permiso permiso) {
        Authentication autenticacion = autenticacion();
        if (autenticacion == null) {
            return true;
        }
        String codigo = permiso.toString();
        for (GrantedAuthority autoridad : autenticacion.getAuthorities()) {
            if (codigo.equals(autoridad.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    public boolean puede(Recurso recurso, Accion accion) {
        return puede(Permiso.de(recurso, accion));
    }

    public boolean puedeTodos(Collection<Permiso> permisos) {
        for (Permiso permiso : permisos) {
            if (!puede(permiso)) {
                return false;
            }
        }
        return true;
    }

    public void exigir(Recurso recurso, Accion accion) {
        if (!puede(recurso, accion)) {
            throw new AccesoDenegadoExcepcion();
        }
    }

    private static Authentication autenticacion() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
