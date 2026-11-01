package com.sixt.servicio;

import com.sixt.dto.DatosCliente;
import com.sixt.dto.NuevoCliente;
import com.sixt.excepcion.AccesoDenegadoExcepcion;
import com.sixt.excepcion.ConflictoExcepcion;
import com.sixt.modelo.Persona;
import com.sixt.modelo.Permiso;
import com.sixt.modelo.Rol;
import com.sixt.repositorio.PersonaRepositorio;
import com.sixt.repositorio.RolRepositorio;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteServicio {
    private final PersonaServicio personas;
    private final PersonaRepositorio repositorio;
    private final RolRepositorio roles;

    @Transactional(readOnly = true)
    public List<Persona> listar() {
        return repositorio.findByRolNombreIgnoreCaseAndFechaBajaIsNull("CLIENTE").stream()
                .filter(p -> esRolCliente(p.getRol())).toList();
    }

    @Transactional
    public Persona crear(NuevoCliente datos) {
        Rol rol = roles.findByNombreIgnoreCase("CLIENTE")
                .filter(this::esRolCliente)
                .orElseThrow(() -> new ConflictoExcepcion("No hay un rol CLIENTE configurado solo con ALQUILAR"));
        return personas.crear(datos.comoPersona(rol.getId()));
    }

    @Transactional
    public Persona actualizar(Long id, DatosCliente datos) {
        Persona cliente = buscarCliente(id);
        return personas.actualizar(id, datos.comoPersona(cliente.getRol().getId()));
    }

    @Transactional
    public void darDeBaja(Long id) {
        buscarCliente(id);
        personas.darDeBaja(id);
    }

    private Persona buscarCliente(Long id) {
        Persona persona = personas.buscarActiva(id);
        if (!esRolCliente(persona.getRol())) throw new AccesoDenegadoExcepcion();
        return persona;
    }

    private boolean esRolCliente(Rol rol) {
        return "CLIENTE".equalsIgnoreCase(rol.getNombre())
                && rol.getPermisos().equals(Set.of(Permiso.ALQUILAR));
    }
}
