package com.sixt.controlador;

import com.sixt.dto.DatosPersona;
import com.sixt.dto.NuevaPersona;
import com.sixt.modelo.Persona;
import com.sixt.servicio.PersonaServicio;
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
@RequestMapping("/api/personas")
@RequiredArgsConstructor
public class PersonaControlador {

    private final PersonaServicio personaServicio;

    @GetMapping
    public List<Persona> listar() {
        return personaServicio.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Persona crear(@Valid @RequestBody NuevaPersona datos) {
        return personaServicio.crear(datos);
    }

    // PUT: reemplaza todos los datos editables (hay que mandarlos todos)
    @PutMapping("/{id}")
    public Persona actualizar(@PathVariable Long id, @Valid @RequestBody DatosPersona datos) {
        return personaServicio.actualizar(id, datos);
    }

    // Baja logica: responde 204 (sin cuerpo)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void darDeBaja(@PathVariable Long id) {
        personaServicio.darDeBaja(id);
    }
}
