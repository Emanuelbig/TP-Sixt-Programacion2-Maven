package com.sixt.controlador;

import com.sixt.dto.DatosOficina;
import com.sixt.modelo.Oficina;
import com.sixt.servicio.OficinaServicio;
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
@RequestMapping("/api/oficinas")
@RequiredArgsConstructor
public class OficinaControlador {

    private final OficinaServicio oficinaServicio;

    @GetMapping
    public List<Oficina> listar() {
        return oficinaServicio.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Oficina crear(@Valid @RequestBody DatosOficina datos) {
        return oficinaServicio.crear(datos);
    }

    @PutMapping("/{id}")
    public Oficina actualizar(@PathVariable Long id, @Valid @RequestBody DatosOficina datos) {
        return oficinaServicio.actualizar(id, datos);
    }

    // Baja logica: responde 204 (sin cuerpo)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void darDeBaja(@PathVariable Long id) {
        oficinaServicio.darDeBaja(id);
    }
}
