package ar.edu.utn.inspt.sixt.controller;

import ar.edu.utn.inspt.sixt.entity.Model;
import ar.edu.utn.inspt.sixt.entity.Vehicle;
import ar.edu.utn.inspt.sixt.repository.ModelRepository;
import ar.edu.utn.inspt.sixt.repository.OfficeRepository;
import ar.edu.utn.inspt.sixt.repository.VehicleRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VehiculoController {

    private final VehicleRepository vehicleRepository;
    private final ModelRepository modelRepository;
    private final OfficeRepository officeRepository;

    public record VehiculoRequest(String plate, String colour, int year, Long modelId, Long officeId) {}

    @GetMapping("/vehiculos")
    public List<Vehicle> listar() {
        return vehicleRepository.findAll();
    }

    @GetMapping("/modelos")
    public List<Model> modelos() {
        return modelRepository.findAll();
    }

    @PostMapping("/vehiculos")
    public Vehicle crear(@RequestBody VehiculoRequest request) {
        Vehicle vehicle = new Vehicle();
        vehicle.setPlate(request.plate().toUpperCase());
        vehicle.setColour(request.colour());
        vehicle.setYear(request.year());
        vehicle.setModel(modelRepository.findById(request.modelId()).orElseThrow());
        vehicle.setOffice(officeRepository.findById(request.officeId()).orElseThrow());
        vehicle.setCreatedAt(Instant.now());
        return vehicleRepository.save(vehicle);
    }
}
