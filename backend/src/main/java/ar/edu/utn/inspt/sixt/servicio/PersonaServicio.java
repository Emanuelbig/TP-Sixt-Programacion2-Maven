package ar.edu.utn.inspt.sixt.servicio;

import ar.edu.utn.inspt.sixt.dto.CambioContrasenia;
import ar.edu.utn.inspt.sixt.dto.DatosPersona;
import ar.edu.utn.inspt.sixt.dto.NuevaPersona;
import ar.edu.utn.inspt.sixt.excepcion.ConflictoExcepcion;
import ar.edu.utn.inspt.sixt.excepcion.DatosInvalidosExcepcion;
import ar.edu.utn.inspt.sixt.excepcion.RecursoNoEncontradoExcepcion;
import ar.edu.utn.inspt.sixt.modelo.Persona;
import ar.edu.utn.inspt.sixt.repositorio.PersonaRepositorio;
import ar.edu.utn.inspt.sixt.repositorio.ReservaRepositorio;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PersonaServicio {

    private final PersonaRepositorio personaRepositorio;
    private final ReservaRepositorio reservaRepositorio;
    private final RolServicio rolServicio;
    private final PasswordEncoder codificador;

    // Solo las que no fueron dadas de baja
    public List<Persona> listar() {
        return personaRepositorio.findByFechaBajaIsNull();
    }

    public Persona buscar(Long id) {
        return personaRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("la persona", id));
    }

    // Para operar (reservar, editar) la persona tiene que seguir activa
    public Persona buscarActiva(Long id) {
        Persona persona = buscar(id);
        if (!persona.estaActiva()) {
            throw new DatosInvalidosExcepcion(persona.getNombre() + " fue dada de baja");
        }
        return persona;
    }

    @Transactional
    public Persona crear(NuevaPersona nueva) {
        DatosPersona datos = nueva.datos();
        if (personaRepositorio.existsByDni(datos.dni())) {
            throw new ConflictoExcepcion("Ya existe una persona con el DNI " + datos.dni());
        }
        if (personaRepositorio.existsByEmailIgnoreCase(datos.email().trim())) {
            throw new ConflictoExcepcion("Ya existe una persona con el email " + normalizarEmail(datos.email()));
        }

        Persona persona = new Persona();
        persona.setContrasenia(codificador.encode(nueva.contrasenia()));
        return guardar(persona, datos);
    }

    @Transactional
    public Persona actualizar(Long id, DatosPersona datos) {
        Persona persona = buscarActiva(id);
        if (personaRepositorio.existsByDniAndIdNot(datos.dni(), id)) {
            throw new ConflictoExcepcion("Ya existe otra persona con el DNI " + datos.dni());
        }
        if (personaRepositorio.existsByEmailIgnoreCaseAndIdNot(datos.email().trim(), id)) {
            throw new ConflictoExcepcion("Ya existe otra persona con el email " + normalizarEmail(datos.email()));
        }
        return guardar(persona, datos);
    }

    @Transactional
    public void cambiarContrasenia(Long id, CambioContrasenia cambio) {
        Persona persona = buscarActiva(id);
        if (!codificador.matches(cambio.contraseniaActual(), persona.getContrasenia())) {
            throw new DatosInvalidosExcepcion("La contraseña actual no es correcta");
        }
        persona.setContrasenia(codificador.encode(cambio.contraseniaNueva()));
    }

    // Baja logica: se marca la fecha y no puede entrar mas, pero sus reservas viejas siguen intactas
    @Transactional
    public void darDeBaja(Long id) {
        Persona persona = buscarActiva(id);
        if (reservaRepositorio.existsByClienteIdAndDevueltoFalse(id)) {
            throw new ConflictoExcepcion(persona.getNombre() + " tiene reservas sin devolver, no se puede dar de baja");
        }
        persona.setFechaBaja(Instant.now());
    }

    private Persona guardar(Persona persona, DatosPersona datos) {
        persona.setDni(datos.dni());
        persona.setNombre(datos.nombre().trim());
        persona.setEmail(normalizarEmail(datos.email()));
        persona.setTelefono(datos.telefono());
        persona.setRol(rolServicio.buscar(datos.rolId()));
        return personaRepositorio.save(persona);
    }

    private static String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }
}
