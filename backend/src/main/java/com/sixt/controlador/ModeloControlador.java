package com.sixt.controlador;

import com.sixt.dto.DatosModelo;
import com.sixt.dto.DatosTipoModelo;
import com.sixt.modelo.Modelo;
import com.sixt.modelo.TipoModelo;
import com.sixt.servicio.ModeloServicio;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Catalogo de modelos y tipos (con el precio por dia).
// Ver los modelos lo puede hacer el vendedor; dar de alta y modificar, solo el admin (AutorizacionApi)
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ModeloControlador {

    private final ModeloServicio modeloServicio;

    @GetMapping("/modelos")
    public List<Modelo> listarModelos() {
        return modeloServicio.listarModelos();
    }

    @PostMapping("/modelos")
    @ResponseStatus(HttpStatus.CREATED)
    public Modelo crearModelo(@Valid @RequestBody DatosModelo datos) {
        return modeloServicio.crearModelo(datos);
    }

    @PutMapping("/modelos/{id}")
    public Modelo actualizarModelo(@PathVariable Long id, @Valid @RequestBody DatosModelo datos) {
        return modeloServicio.actualizarModelo(id, datos);
    }

    @GetMapping("/tipos")
    public List<TipoModelo> listarTipos() {
        return modeloServicio.listarTipos();
    }

    @PostMapping("/tipos")
    @ResponseStatus(HttpStatus.CREATED)
    public TipoModelo crearTipo(@Valid @RequestBody DatosTipoModelo datos) {
        return modeloServicio.crearTipo(datos);
    }

    @PutMapping("/tipos/{id}")
    public TipoModelo actualizarTipo(@PathVariable Long id, @Valid @RequestBody DatosTipoModelo datos) {
        return modeloServicio.actualizarTipo(id, datos);
    }
}
