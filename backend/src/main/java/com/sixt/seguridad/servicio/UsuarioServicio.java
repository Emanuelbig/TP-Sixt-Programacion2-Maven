package com.sixt.seguridad.servicio;

import com.sixt.excepcion.ConflictoExcepcion;
import com.sixt.excepcion.CredencialesInvalidasExcepcion;
import com.sixt.excepcion.DatosInvalidosExcepcion;
import com.sixt.excepcion.RecursoNoEncontradoExcepcion;
import com.sixt.seguridad.dto.CambioContrasenia;
import com.sixt.seguridad.modelo.Rol;
import com.sixt.seguridad.modelo.Usuario;
import com.sixt.seguridad.repositorio.UsuarioRepositorio;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ABM de las cuentas de acceso. Las reglas de privilegios las decide ControlDePrivilegios.
@Service
@RequiredArgsConstructor
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder codificador;
    private final ControlDePrivilegios control;

    public Usuario buscar(Long id) {
        Usuario usuario = usuarioRepositorio.findById(id).orElse(null);
        if (usuario == null) {
            throw new RecursoNoEncontradoExcepcion("el usuario", id);
        }
        return usuario;
    }

    @Transactional
    public Usuario crear(String email, String contrasenia, Rol rol) {
        String normalizado = normalizarEmail(email);
        control.exigirPuedeOtorgar(rol);
        if (usuarioRepositorio.existsByEmailIgnoreCase(normalizado)) {
            throw new ConflictoExcepcion("Ya existe un usuario con el email " + normalizado);
        }
        Usuario usuario = new Usuario();
        usuario.setEmail(normalizado);
        usuario.setContrasenia(codificador.encode(contrasenia));
        usuario.setRol(rol);
        return usuarioRepositorio.save(usuario);
    }

    @Transactional
    public Usuario actualizar(Long id, String email, Rol rol) {
        Usuario usuario = buscar(id);
        String normalizado = normalizarEmail(email);
        control.exigirPuedeModificar(usuario);
        if (!usuario.getRol().getId().equals(rol.getId())) {
            control.exigirQueNoSeaElMismo(id, "No podes cambiar tu propio rol");
            control.exigirPuedeOtorgar(rol);
        }
        if (usuarioRepositorio.existsByEmailIgnoreCaseAndIdNot(normalizado, id)) {
            throw new ConflictoExcepcion("Ya existe otro usuario con el email " + normalizado);
        }
        usuario.setEmail(normalizado);
        usuario.setRol(rol);
        control.verificarQueQuedaUnAdministradorDeRoles();
        return usuario;
    }

    @Transactional
    public void cambiarContrasenia(Long id, CambioContrasenia cambio) {
        Usuario usuario = buscar(id);
        if (!codificador.matches(cambio.contraseniaActual(), usuario.getContrasenia())) {
            throw new DatosInvalidosExcepcion("La contraseña actual no es correcta");
        }
        usuario.setContrasenia(codificador.encode(cambio.contraseniaNueva()));
    }

    // Un usuario dado de baja deja de poder entrar aunque su token no haya vencido
    @Transactional
    public void desactivar(Long id) {
        control.exigirQueNoSeaElMismo(id, "No podes darte de baja a vos mismo");
        Usuario usuario = buscar(id);
        control.exigirPuedeModificar(usuario);
        usuario.desactivar();
        control.verificarQueQuedaUnAdministradorDeRoles();
    }

    @Transactional
    public void cerrarSesion(Long id) {
        buscar(id).invalidarTokens();
    }

    // Lo usa ConvertidorDeToken en cada pedido. Si el usuario ya no existe, fue dado
    // de baja o el token es de una sesion cerrada, el token deja de valer.
    @Transactional(readOnly = true)
    public Set<String> permisosVigentes(Long id, int versionToken) {
        Usuario usuario = usuarioRepositorio.findById(id).orElse(null);
        if (usuario == null || !usuario.aceptaToken(versionToken)) {
            throw new CredencialesInvalidasExcepcion();
        }
        return usuario.getRol().codigosDePermisosEfectivos();
    }

    public boolean hayUsuariosConRol(Long rolId) {
        return usuarioRepositorio.existsByRolId(rolId);
    }

    private static String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }
}
