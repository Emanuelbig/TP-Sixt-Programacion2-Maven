package com.sixt.controlador;

import com.sixt.dto.NuevoRol;
import com.sixt.dto.PermisoInfo;
import com.sixt.modelo.Rol;
import com.sixt.servicio.RolServicio;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RolControlador {

    private final RolServicio rolServicio;

    @GetMapping("/roles")
    public List<Rol> listar() {
        return rolServicio.listar();
    }

    @GetMapping("/permisos")
    public List<PermisoInfo> permisos() {
        return rolServicio.listarPermisos();
    }

    @PostMapping("/roles")
    @ResponseStatus(HttpStatus.CREATED)
    public Rol crear(@Valid @RequestBody NuevoRol datos) {
        return rolServicio.crear(datos);
    }

    @PutMapping("/roles/{id}")
    public Rol actualizar(@PathVariable Long id, @Valid @RequestBody NuevoRol datos) {
        return rolServicio.actualizar(id, datos);
    }

    @DeleteMapping("/roles/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        rolServicio.eliminar(id);
    }
}
