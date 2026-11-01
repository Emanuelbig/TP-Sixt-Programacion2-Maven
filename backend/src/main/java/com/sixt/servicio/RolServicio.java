package com.sixt.servicio;

import com.sixt.dto.NuevoRol;
import com.sixt.dto.PermisoInfo;
import com.sixt.excepcion.ConflictoExcepcion;
import com.sixt.excepcion.RecursoNoEncontradoExcepcion;
import com.sixt.modelo.Permiso;
import com.sixt.modelo.Rol;
import com.sixt.repositorio.PersonaRepositorio;
import com.sixt.repositorio.RolRepositorio;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RolServicio {

    private final RolRepositorio rolRepositorio;
    private final PersonaRepositorio personaRepositorio;

    public List<Rol> listar() {
        return rolRepositorio.findAll();
    }

    public List<PermisoInfo> listarPermisos() {
        return Arrays.stream(Permiso.values()).map(PermisoInfo::de).toList();
    }

    public Rol buscar(Long id) {
        return rolRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("el rol", id));
    }

    @Transactional
    public Rol crear(NuevoRol datos) {
        String nombre = datos.nombre().trim().toUpperCase();

        if (rolRepositorio.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictoExcepcion("Ya existe el rol " + nombre);
        }

        Rol rol = new Rol();
        rol.setNombre(nombre);
        rol.setPermisos(EnumSet.copyOf(datos.permisos()));
        return rolRepositorio.save(rol);
    }

    @Transactional
    public Rol actualizar(Long id, NuevoRol datos) {
        Rol rol = buscar(id);
        String nombre = datos.nombre().trim().toUpperCase();
        if (rolRepositorio.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ConflictoExcepcion("Ya existe otro rol llamado " + nombre);
        }
        // Si este rol pierde ADMINISTRAR, tiene que quedar otra persona activa que lo tenga;
        // si no, nadie podria volver a entrar a gestionar roles
        boolean pierdeAdministrar = rol.tiene(Permiso.ADMINISTRAR) && !datos.permisos().contains(Permiso.ADMINISTRAR);
        if (pierdeAdministrar && !personaRepositorio.existeOtroActivoConPermiso(Permiso.ADMINISTRAR, id)) {
            throw new ConflictoExcepcion("No se puede quitar ADMINISTRAR: nadie mas podria administrar el sistema");
        }
        rol.setNombre(nombre);
        rol.setPermisos(EnumSet.copyOf(datos.permisos()));
        return rolRepositorio.save(rol);
    }

    // Los roles se borran de verdad (no hay baja logica) porque solo se permite si nadie los usa
    @Transactional
    public void eliminar(Long id) {
        Rol rol = buscar(id);
        if (personaRepositorio.existsByRolId(id)) {
            throw new ConflictoExcepcion("El rol " + rol.getNombre() + " esta asignado a personas, no se puede eliminar");
        }
        rolRepositorio.delete(rol);
    }
}
