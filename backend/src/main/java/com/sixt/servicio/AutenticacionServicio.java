package com.sixt.servicio;

import com.sixt.dto.DatosLogin;
import com.sixt.dto.UsuarioLogueado;
import com.sixt.excepcion.CredencialesInvalidasExcepcion;
import com.sixt.modelo.Persona;
import com.sixt.repositorio.PersonaRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AutenticacionServicio {

    private final PersonaRepositorio personaRepositorio;
    private final PasswordEncoder codificador;

    // Si el email no existe, la contraseña no coincide o la persona fue dada de baja,
    // se responde siempre lo mismo: asi no se puede averiguar que emails estan registrados.
    @Transactional(readOnly = true)
    public UsuarioLogueado login(DatosLogin datos) {
        Persona persona = personaRepositorio.findByEmailIgnoreCase(datos.email().trim())
                .filter(Persona::estaActiva)
                .filter(p -> codificador.matches(datos.contrasenia(), p.getContrasenia()))
                .orElseThrow(CredencialesInvalidasExcepcion::new);
        return UsuarioLogueado.de(persona);
    }
    @Transactional(readOnly = true)
    public UsuarioLogueado usuarioDeSesion(Long id) {
        return personaRepositorio.findById(id).filter(Persona::estaActiva)
                .map(UsuarioLogueado::de).orElseThrow(CredencialesInvalidasExcepcion::new);
    }

}
