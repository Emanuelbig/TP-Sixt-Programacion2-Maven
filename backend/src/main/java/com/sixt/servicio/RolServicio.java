package com.sixt.servicio;

import com.sixt.dto.NuevoRol;
import com.sixt.dto.PermisoInfo;
import com.sixt.excepcion.ConflictoExcepcion;
import com.sixt.excepcion.RecursoNoEncontradoExcepcion;
import com.sixt.modelo.Permiso;
import com.sixt.modelo.Rol;
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
}
