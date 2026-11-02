package com.sixt.seguridad.servicio;

import com.sixt.excepcion.ConflictoExcepcion;
import com.sixt.excepcion.DatosInvalidosExcepcion;
import com.sixt.excepcion.RecursoNoEncontradoExcepcion;
import com.sixt.seguridad.dto.DatosRol;
import com.sixt.seguridad.dto.RecursoInfo;
import com.sixt.seguridad.modelo.Permiso;
import com.sixt.seguridad.modelo.Recurso;
import com.sixt.seguridad.modelo.Rol;
import com.sixt.seguridad.repositorio.RolRepositorio;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ABM de roles. Las reglas de privilegios las decide ControlDePrivilegios.
@Service
@RequiredArgsConstructor
public class RolServicio {

    private final RolRepositorio rolRepositorio;
    private final UsuarioServicio usuarioServicio;
    private final ControlDePrivilegios control;

    public List<Rol> listar() {
        return rolRepositorio.findAll();
    }

    // Catalogo de recursos y acciones para armar la matriz de la pantalla de roles
    public List<RecursoInfo> catalogo() {
        List<RecursoInfo> catalogo = new ArrayList<>();
        for (Recurso recurso : Recurso.values()) {
            catalogo.add(RecursoInfo.de(recurso));
        }
        return catalogo;
    }

    public Rol buscar(Long id) {
        Rol rol = rolRepositorio.findById(id).orElse(null);
        if (rol == null) {
            throw new RecursoNoEncontradoExcepcion("el rol", id);
        }
        return rol;
    }

    public Rol buscarPorNombre(String nombre) {
        Rol rol = rolRepositorio.findByNombreIgnoreCase(nombre).orElse(null);
        if (rol == null) {
            throw new RecursoNoEncontradoExcepcion("No existe el rol " + nombre);
        }
        return rol;
    }

    @Transactional
    public Rol crear(DatosRol datos) {
        String nombre = normalizarNombre(datos.nombre());
        if (rolRepositorio.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictoExcepcion("Ya existe el rol " + nombre);
        }
        Rol rol = new Rol(nombre, convertir(datos.permisos()));
        control.exigirPuedeOtorgar(rol);
        return rolRepositorio.save(rol);
    }

    // Hay que tener los permisos que el rol ya tenia y los nuevos:
    // asi nadie puede "vaciar" el rol de alguien con mas permisos.
    @Transactional
    public Rol actualizar(Long id, DatosRol datos) {
        Rol rol = buscar(id);
        control.exigirQueNoSeaSuPropioRol(rol);
        control.exigirPuedeOtorgar(rol);
        String nombre = normalizarNombre(datos.nombre());
        if (rolRepositorio.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ConflictoExcepcion("Ya existe otro rol llamado " + nombre);
        }
        Rol nuevo = new Rol(nombre, convertir(datos.permisos()));
        control.exigirPuedeOtorgar(nuevo);
        rol.setNombre(nombre);
        rol.reemplazarPermisos(nuevo.getPermisos());
        control.verificarQueQuedaUnAdministradorDeRoles();
        return rol;
    }

    @Transactional
    public void eliminar(Long id) {
        Rol rol = buscar(id);
        control.exigirQueNoSeaSuPropioRol(rol);
        control.exigirPuedeOtorgar(rol);
        if (usuarioServicio.hayUsuariosConRol(id)) {
            throw new ConflictoExcepcion("El rol " + rol.getNombre() + " tiene usuarios asignados, no se puede borrar");
        }
        rolRepositorio.delete(rol);
    }

    private static Set<Permiso> convertir(Set<String> codigos) {
        Set<Permiso> permisos = new HashSet<>();
        for (String codigo : codigos) {
            Permiso permiso;
            try {
                permiso = Permiso.de(codigo);
            } catch (IllegalArgumentException e) {
                throw new DatosInvalidosExcepcion("El permiso " + codigo + " no existe");
            }
            if (!permiso.esValido()) {
                throw new DatosInvalidosExcepcion("El recurso " + permiso.getRecurso()
                        + " no admite la accion " + permiso.getAccion());
            }
            permisos.add(permiso);
        }
        return permisos;
    }

    private static String normalizarNombre(String nombre) {
        return nombre.trim().toUpperCase();
    }
}
