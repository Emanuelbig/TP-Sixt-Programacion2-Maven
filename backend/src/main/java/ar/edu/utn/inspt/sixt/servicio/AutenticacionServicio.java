package ar.edu.utn.inspt.sixt.servicio;

import ar.edu.utn.inspt.sixt.dto.DatosLogin;
import ar.edu.utn.inspt.sixt.dto.UsuarioLogueado;
import ar.edu.utn.inspt.sixt.excepcion.CredencialesInvalidasExcepcion;
import ar.edu.utn.inspt.sixt.modelo.Persona;
import ar.edu.utn.inspt.sixt.repositorio.PersonaRepositorio;
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
}
