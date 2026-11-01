package com.sixt.controlador;

import com.sixt.dto.DatosCliente;
import com.sixt.dto.NuevoCliente;
import com.sixt.modelo.Persona;
import com.sixt.servicio.ClienteServicio;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteControlador {
    private final ClienteServicio clientes;

    @GetMapping
    public List<Persona> listar() { return clientes.listar(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Persona crear(@Valid @RequestBody NuevoCliente datos) { return clientes.crear(datos); }

    @PutMapping("/{id}")
    public Persona actualizar(@PathVariable Long id, @Valid @RequestBody DatosCliente datos) {
        return clientes.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void darDeBaja(@PathVariable Long id) { clientes.darDeBaja(id); }
}
