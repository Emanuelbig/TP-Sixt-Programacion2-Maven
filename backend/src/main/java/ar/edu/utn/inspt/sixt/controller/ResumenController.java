package ar.edu.utn.inspt.sixt.controller;

import ar.edu.utn.inspt.sixt.repository.BookingRepository;
import ar.edu.utn.inspt.sixt.repository.OfficeRepository;
import ar.edu.utn.inspt.sixt.repository.PersonRepository;
import ar.edu.utn.inspt.sixt.repository.VehicleRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Contadores para la pantalla de inicio
@RestController
@RequestMapping("/api/resumen")
@RequiredArgsConstructor
public class ResumenController {

    private final OfficeRepository officeRepository;
    private final VehicleRepository vehicleRepository;
    private final PersonRepository personRepository;
    private final BookingRepository bookingRepository;

    @GetMapping
    public Map<String, Long> resumen() {
        return Map.of(
                "oficinas", officeRepository.count(),
                "vehiculos", vehicleRepository.count(),
                "personas", personRepository.count(),
                "reservas", bookingRepository.count());
    }
}
