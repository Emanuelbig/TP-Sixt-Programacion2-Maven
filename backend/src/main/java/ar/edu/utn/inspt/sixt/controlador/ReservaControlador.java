package ar.edu.utn.inspt.sixt.controlador;

import ar.edu.utn.inspt.sixt.dto.NuevaReserva;
import ar.edu.utn.inspt.sixt.modelo.Reserva;
import ar.edu.utn.inspt.sixt.servicio.ReservaServicio;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaControlador {

    private final ReservaServicio reservaServicio;

    // GET /api/reservas            -> todas
    // GET /api/reservas?clienteId=3 -> solo las de ese cliente ("Mis reservas")
    @GetMapping
    public List<Reserva> listar(@RequestParam(required = false) Long clienteId) {
        return clienteId == null ? reservaServicio.listar() : reservaServicio.listarDeCliente(clienteId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reserva crear(@Valid @RequestBody NuevaReserva datos) {
        return reservaServicio.crear(datos);
    }

    @PostMapping("/{id}/devolver")
    public Reserva devolver(@PathVariable Long id) {
        return reservaServicio.devolver(id);
    }
}
