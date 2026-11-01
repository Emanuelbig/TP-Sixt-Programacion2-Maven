package com.sixt.controlador;

import com.sixt.dto.DatosVehiculo;
import com.sixt.modelo.Vehiculo;
import com.sixt.servicio.VehiculoServicio;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VehiculoControlador {

    private final VehiculoServicio vehiculoServicio;

    @GetMapping("/vehiculos")
    public List<Vehiculo> listar() {
        return vehiculoServicio.listar();
    }

    // GET /api/vehiculos/disponibles?desde=2026-11-10&hasta=2026-11-12
    // Los activos que no tienen una reserva sin devolver que se pise con esas fechas
    @GetMapping("/vehiculos/disponibles")
    public List<Vehiculo> disponibles(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return vehiculoServicio.listarDisponibles(desde, hasta);
    }

    @PostMapping("/vehiculos")
    @ResponseStatus(HttpStatus.CREATED)
    public Vehiculo crear(@Valid @RequestBody DatosVehiculo datos) {
        return vehiculoServicio.crear(datos);
    }

    @PutMapping("/vehiculos/{id}")
    public Vehiculo actualizar(@PathVariable Long id, @Valid @RequestBody DatosVehiculo datos) {
        return vehiculoServicio.actualizar(id, datos);
    }

    // Baja logica: responde 204 (sin cuerpo)
    @DeleteMapping("/vehiculos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void darDeBaja(@PathVariable Long id) {
        vehiculoServicio.darDeBaja(id);
    }
}
