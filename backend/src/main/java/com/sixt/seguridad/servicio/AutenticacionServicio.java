package com.sixt.seguridad.servicio;

import com.sixt.excepcion.CredencialesInvalidasExcepcion;
import com.sixt.seguridad.dto.DatosLogin;
import com.sixt.seguridad.dto.RespuestaLogin;
import com.sixt.seguridad.modelo.Usuario;
import com.sixt.seguridad.repositorio.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AutenticacionServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder codificador;
    private final TokenServicio tokenServicio;

    // Si el email no existe, la contraseña no coincide o el usuario fue dado de baja,
    // se responde siempre lo mismo: asi no se puede averiguar que emails estan registrados.
    @Transactional(readOnly = true)
    public RespuestaLogin login(DatosLogin datos) {
        Usuario usuario = usuarioRepositorio.findByEmailIgnoreCase(datos.email().trim()).orElse(null);
        if (usuario == null || !usuario.isActivo()
                || !codificador.matches(datos.contrasenia(), usuario.getContrasenia())) {
            throw new CredencialesInvalidasExcepcion();
        }
        return new RespuestaLogin(tokenServicio.emitir(usuario), usuario.getId(), usuario.getEmail(),
                usuario.getRol().getNombre(), usuario.getRol().codigosDePermisosEfectivos());
    }
}
