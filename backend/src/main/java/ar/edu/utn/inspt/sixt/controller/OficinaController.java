package ar.edu.utn.inspt.sixt.controller;

import ar.edu.utn.inspt.sixt.entity.Office;
import ar.edu.utn.inspt.sixt.repository.OfficeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/oficinas")
@RequiredArgsConstructor
public class OficinaController {

    private final OfficeRepository officeRepository;

    public record OficinaRequest(String zone, String address) {}

    @GetMapping
    public List<Office> listar() {
        return officeRepository.findAll();
    }

    @PostMapping
    public Office crear(@RequestBody OficinaRequest request) {
        Office office = new Office();
        office.setZone(request.zone());
        office.setAddress(request.address());
        return officeRepository.save(office);
    }
}
