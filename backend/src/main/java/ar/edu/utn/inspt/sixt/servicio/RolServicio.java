package ar.edu.utn.inspt.sixt.servicio;

import ar.edu.utn.inspt.sixt.dto.NuevoRol;
import ar.edu.utn.inspt.sixt.dto.PermisoInfo;
import ar.edu.utn.inspt.sixt.excepcion.ConflictoExcepcion;
import ar.edu.utn.inspt.sixt.excepcion.RecursoNoEncontradoExcepcion;
import ar.edu.utn.inspt.sixt.modelo.Permiso;
import ar.edu.utn.inspt.sixt.modelo.Rol;
import ar.edu.utn.inspt.sixt.repositorio.RolRepositorio;
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
