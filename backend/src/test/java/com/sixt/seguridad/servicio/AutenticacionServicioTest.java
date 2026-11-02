package com.sixt.seguridad.servicio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sixt.excepcion.CredencialesInvalidasExcepcion;
import com.sixt.seguridad.dto.DatosLogin;
import com.sixt.seguridad.dto.RespuestaLogin;
import com.sixt.seguridad.modelo.Accion;
import com.sixt.seguridad.modelo.Permiso;
import com.sixt.seguridad.modelo.Recurso;
import com.sixt.seguridad.modelo.Rol;
import com.sixt.seguridad.modelo.Usuario;
import com.sixt.seguridad.repositorio.UsuarioRepositorio;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// El repositorio y el token estan simulados, pero BCrypt es el real: asi se prueba el hash de verdad
class AutenticacionServicioTest {

    private final UsuarioRepositorio usuarioRepositorio = mock(UsuarioRepositorio.class);
    private final TokenServicio tokenServicio = mock(TokenServicio.class);
    private final PasswordEncoder codificador = new BCryptPasswordEncoder();
    private final AutenticacionServicio servicio =
            new AutenticacionServicio(usuarioRepositorio, codificador, tokenServicio);

    private Usuario ema;

    @BeforeEach
    void cargarUsuario() {
        Rol cliente = new Rol("CLIENTE", Set.of(
                Permiso.de(Recurso.RESERVA, Accion.VER_PROPIAS), Permiso.de(Recurso.RESERVA, Accion.ALQUILAR)));
        ema = new Usuario();
        ema.setId(3L);
        ema.setEmail("ema@gmail.com");
        ema.setContrasenia(codificador.encode("cliente123"));
        ema.setRol(cliente);
        when(usuarioRepositorio.findByEmailIgnoreCase("ema@gmail.com")).thenReturn(Optional.of(ema));
        when(tokenServicio.emitir(any())).thenReturn("token-de-prueba");
    }

    @Test
    void conEmailYContraseniaCorrectosDevuelveElTokenYLosPermisos() {
        RespuestaLogin respuesta = servicio.login(new DatosLogin("ema@gmail.com", "cliente123"));

        assertThat(respuesta.token()).isEqualTo("token-de-prueba");
        assertThat(respuesta.id()).isEqualTo(3L);
        assertThat(respuesta.rol()).isEqualTo("CLIENTE");
        assertThat(respuesta.permisos()).containsExactly("RESERVA:ALQUILAR", "RESERVA:VER_PROPIAS");
    }

    @Test
    void laContraseniaSeGuardaHasheadaYNoEnTextoPlano() {
        assertThat(ema.getContrasenia()).isNotEqualTo("cliente123").startsWith("$2a$");
    }

    @Test
    void conContraseniaIncorrectaRechaza() {
        assertThatThrownBy(() -> servicio.login(new DatosLogin("ema@gmail.com", "otra")))
                .isInstanceOf(CredencialesInvalidasExcepcion.class);
    }

    @Test
    void conEmailInexistenteRechazaConElMismoMensaje() {
        when(usuarioRepositorio.findByEmailIgnoreCase("nadie@gmail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.login(new DatosLogin("nadie@gmail.com", "cliente123")))
                .isInstanceOf(CredencialesInvalidasExcepcion.class)
                .hasMessage("Email o contraseña incorrectos");
    }

    @Test
    void unUsuarioDadoDeBajaNoPuedeEntrar() {
        ema.setActivo(false);

        assertThatThrownBy(() -> servicio.login(new DatosLogin("ema@gmail.com", "cliente123")))
                .isInstanceOf(CredencialesInvalidasExcepcion.class);
    }
}
