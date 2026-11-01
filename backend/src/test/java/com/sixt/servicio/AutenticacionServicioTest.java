package com.sixt.servicio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sixt.dto.DatosLogin;
import com.sixt.dto.UsuarioLogueado;
import com.sixt.excepcion.CredencialesInvalidasExcepcion;
import com.sixt.modelo.Permiso;
import com.sixt.modelo.Persona;
import com.sixt.modelo.Rol;
import com.sixt.repositorio.PersonaRepositorio;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// El repositorio esta simulado, pero BCrypt es el real: asi se prueba el hash de verdad
class AutenticacionServicioTest {

    private final PersonaRepositorio personaRepositorio = mock(PersonaRepositorio.class);
    private final PasswordEncoder codificador = new BCryptPasswordEncoder();
    private final AutenticacionServicio servicio = new AutenticacionServicio(personaRepositorio, codificador);

    private Persona ema;

    @BeforeEach
    void cargarUsuario() {
        Rol cliente = new Rol();
        cliente.setNombre("CLIENTE");
        cliente.setPermisos(EnumSet.of(Permiso.ALQUILAR));

        ema = new Persona();
        ema.setId(3L);
        ema.setNombre("Ema");
        ema.setEmail("ema@gmail.com");
        ema.setContrasenia(codificador.encode("cliente123"));
        ema.setRol(cliente);
        when(personaRepositorio.findByEmailIgnoreCase("ema@gmail.com")).thenReturn(Optional.of(ema));
    }

    @Test
    void conEmailYContraseniaCorrectosDevuelveElUsuarioConSusPermisos() {
        UsuarioLogueado usuario = servicio.login(new DatosLogin("ema@gmail.com", "cliente123"));

        assertThat(usuario.id()).isEqualTo(3L);
        assertThat(usuario.rol()).isEqualTo("CLIENTE");
        assertThat(usuario.permisos()).containsExactly(Permiso.ALQUILAR);
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
        when(personaRepositorio.findByEmailIgnoreCase("nadie@gmail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.login(new DatosLogin("nadie@gmail.com", "cliente123")))
                .isInstanceOf(CredencialesInvalidasExcepcion.class)
                .hasMessage("Email o contraseña incorrectos");
    }

    @Test
    void unaPersonaDadaDeBajaNoPuedeEntrar() {
        ema.setFechaBaja(Instant.now());

        assertThatThrownBy(() -> servicio.login(new DatosLogin("ema@gmail.com", "cliente123")))
                .isInstanceOf(CredencialesInvalidasExcepcion.class);
    }
}
