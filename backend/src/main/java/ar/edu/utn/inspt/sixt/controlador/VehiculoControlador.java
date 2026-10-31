package ar.edu.utn.inspt.sixt.controlador;

import ar.edu.utn.inspt.sixt.dto.DatosVehiculo;
import ar.edu.utn.inspt.sixt.modelo.Modelo;
import ar.edu.utn.inspt.sixt.modelo.Vehiculo;
import ar.edu.utn.inspt.sixt.servicio.VehiculoServicio;
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
public class VehiculoControlador {

    private final VehiculoServicio vehiculoServicio;

    @GetMapping("/vehiculos")
    public List<Vehiculo> listar() {
        return vehiculoServicio.listar();
    }

    @GetMapping("/modelos")
    public List<Modelo> modelos() {
        return vehiculoServicio.listarModelos();
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
