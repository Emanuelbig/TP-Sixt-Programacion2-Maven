package ar.edu.utn.inspt.sixt.controller;

import ar.edu.utn.inspt.sixt.entity.Booking;
import ar.edu.utn.inspt.sixt.entity.Vehicle;
import ar.edu.utn.inspt.sixt.repository.BookingRepository;
import ar.edu.utn.inspt.sixt.repository.OfficeRepository;
import ar.edu.utn.inspt.sixt.repository.PersonRepository;
import ar.edu.utn.inspt.sixt.repository.VehicleRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final BookingRepository bookingRepository;
    private final PersonRepository personRepository;
    private final VehicleRepository vehicleRepository;
    private final OfficeRepository officeRepository;

    public record ReservaRequest(Long clientId, Long salesmanId, Long vehicleId,
                                 Long originOfficeId, Long destinationOfficeId,
                                 LocalDate dateFrom, LocalDate dateTo, int gasLiters) {}

    @GetMapping
    public List<Booking> listar() {
        return bookingRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ReservaRequest request) {
        if (request.dateTo().isBefore(request.dateFrom())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La fecha de fin no puede ser anterior a la de inicio"));
        }

        Vehicle vehicle = vehicleRepository.findById(request.vehicleId()).orElseThrow();
        long dias = ChronoUnit.DAYS.between(request.dateFrom(), request.dateTo()) + 1;

        Booking booking = new Booking();
        booking.setClient(personRepository.findById(request.clientId()).orElseThrow());
        booking.setSalesman(personRepository.findById(request.salesmanId()).orElseThrow());
        booking.setVehicle(vehicle);
        booking.setPlate(vehicle.getPlate());
        booking.setColour(vehicle.getColour());
        booking.setYear(vehicle.getYear());
        booking.setOriginOffice(officeRepository.findById(request.originOfficeId()).orElseThrow());
        booking.setDestinationOffice(officeRepository.findById(request.destinationOfficeId()).orElseThrow());
        booking.setDateFrom(request.dateFrom());
        booking.setDateTo(request.dateTo());
        booking.setGasLiters(request.gasLiters());
        // precio diario del tipo de modelo * cantidad de dias
        booking.setTotalPrice(vehicle.getModel().getType().getPrice().intValue() * (int) dias);
        booking.setIsReturned(false);
        booking.setCreatedAt(Instant.now());
        return ResponseEntity.ok(bookingRepository.save(booking));
    }

    // Al devolver el auto, queda en la oficina de destino
    @PostMapping("/{id}/devolver")
    public Booking devolver(@PathVariable Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        booking.setIsReturned(true);
        booking.getVehicle().setOffice(booking.getDestinationOffice());
        vehicleRepository.save(booking.getVehicle());
        return bookingRepository.save(booking);
    }
}
