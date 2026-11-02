package com.sixt.servicio;

import com.sixt.dto.DatosPersona;
import com.sixt.dto.NuevaPersona;
import com.sixt.excepcion.ConflictoExcepcion;
import com.sixt.excepcion.DatosInvalidosExcepcion;
import com.sixt.excepcion.RecursoNoEncontradoExcepcion;
import com.sixt.modelo.Persona;
import com.sixt.repositorio.PersonaRepositorio;
import com.sixt.repositorio.ReservaRepositorio;
import com.sixt.seguridad.modelo.Accion;
import com.sixt.seguridad.modelo.Recurso;
import com.sixt.seguridad.modelo.Rol;
import com.sixt.seguridad.modelo.Usuario;
import com.sixt.seguridad.servicio.RolServicio;
import com.sixt.seguridad.servicio.UsuarioActual;
import com.sixt.seguridad.servicio.UsuarioServicio;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Clientes y empleados estan en la misma tabla. Segun el rol de la persona que se toca
// se exige CLIENTE:<accion> o USUARIO:<accion>; asi un vendedor que administra clientes
// no puede dar de alta un admin. Los datos de acceso los maneja UsuarioServicio.
@Service
@RequiredArgsConstructor
public class PersonaServicio {

    private static final String ROL_CLIENTE = "CLIENTE";

    private final PersonaRepositorio personaRepositorio;
    private final ReservaRepositorio reservaRepositorio;
    private final RolServicio rolServicio;
    private final UsuarioServicio usuarioServicio;
    private final UsuarioActual usuarioActual;

    // Solo las que no fueron dadas de baja y que quien pide puede ver
    @Transactional(readOnly = true)
    public List<Persona> listar() {
        List<Persona> visibles = new ArrayList<>();
        for (Persona persona : personaRepositorio.findByFechaBajaIsNull()) {
            if (usuarioActual.puede(recursoDe(persona.getRol()), Accion.VER)) {
                visibles.add(persona);
            }
        }
        return visibles;
    }

    public Persona buscar(Long id) {
        Persona persona = personaRepositorio.findById(id).orElse(null);
        if (persona == null) {
            throw new RecursoNoEncontradoExcepcion("la persona", id);
        }
        return persona;
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
        Rol rol = rolParaAlta(datos.rolId());
        usuarioActual.exigir(recursoDe(rol), Accion.CREAR);
        if (personaRepositorio.existsByDni(datos.dni())) {
            throw new ConflictoExcepcion("Ya existe una persona con el DNI " + datos.dni());
        }
        Usuario usuario = usuarioServicio.crear(datos.email(), nueva.contrasenia(), rol);
        Persona persona = new Persona();
        persona.setUsuario(usuario);
        return guardar(persona, datos);
    }

    @Transactional
    public Persona actualizar(Long id, DatosPersona datos) {
        Persona persona = buscarActiva(id);
        // Sin rol en el pedido (por ejemplo, un vendedor editando un cliente) se conserva el actual
        Rol rol = datos.rolId() == null ? persona.getRol() : rolServicio.buscar(datos.rolId());
        usuarioActual.exigir(recursoDe(persona.getRol()), Accion.EDITAR);
        usuarioActual.exigir(recursoDe(rol), Accion.EDITAR);
        if (personaRepositorio.existsByDniAndIdNot(datos.dni(), id)) {
            throw new ConflictoExcepcion("Ya existe otra persona con el DNI " + datos.dni());
        }
        usuarioServicio.actualizar(id, datos.email(), rol);
        return guardar(persona, datos);
    }

    // Baja logica: se marca la fecha y no puede entrar mas, pero sus reservas viejas siguen intactas
    @Transactional
    public void darDeBaja(Long id) {
        Persona persona = buscarActiva(id);
        usuarioActual.exigir(recursoDe(persona.getRol()), Accion.ELIMINAR);
        if (reservaRepositorio.existsByClienteIdAndDevueltoFalseAndFechaCancelacionIsNull(id)) {
            throw new ConflictoExcepcion(persona.getNombre() + " tiene reservas sin devolver, no se puede dar de baja");
        }
        usuarioServicio.desactivar(id);
        persona.setFechaBaja(Instant.now());
    }

    private Persona guardar(Persona persona, DatosPersona datos) {
        persona.setDni(datos.dni());
        persona.setNombre(datos.nombre().trim());
        persona.setDireccion(datos.direccion().trim());
        persona.setTelefono(datos.telefono());
        return personaRepositorio.save(persona);
    }

    // Sin rol en el pedido el alta es de un cliente: es lo que hace el vendedor,
    // que no ve el campo de rol. El rol lo pone el servidor, nunca el formulario.
    private Rol rolParaAlta(Long rolId) {
        if (rolId != null) {
            return rolServicio.buscar(rolId);
        }
        Rol cliente = rolServicio.buscarPorNombre(ROL_CLIENTE);
        if (!cliente.esDeCliente()) {
            throw new ConflictoExcepcion(
                    "El rol CLIENTE tiene que tener solo RESERVA:VER_PROPIAS y RESERVA:ALQUILAR");
        }
        return cliente;
    }

    private static Recurso recursoDe(Rol rol) {
        return rol.esDeCliente() ? Recurso.CLIENTE : Recurso.USUARIO;
    }
}
