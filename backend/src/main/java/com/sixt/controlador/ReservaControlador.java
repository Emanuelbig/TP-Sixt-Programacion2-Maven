package com.sixt.controlador;

import com.sixt.dto.NuevaReserva;
import com.sixt.excepcion.AccesoDenegadoExcepcion;
import com.sixt.modelo.Reserva;
import com.sixt.seguridad.modelo.Accion;
import com.sixt.seguridad.modelo.Recurso;
import com.sixt.seguridad.servicio.UsuarioActual;
import com.sixt.servicio.ReservaServicio;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final UsuarioActual usuarioActual;

    // GET /api/reservas            -> todas (hace falta RESERVA:VER)
    // GET /api/reservas?clienteId=3 -> solo las de ese cliente ("Mis reservas")
    // Con solo RESERVA:VER_PROPIAS se puede pedir unicamente el propio clienteId:
    // la regla depende del dato del pedido, por eso va en un @PreAuthorize y no en la URL.
    @GetMapping
    @PreAuthorize("hasAuthority('RESERVA:VER') or (#clienteId != null and #clienteId.toString() == authentication.name)")
    public List<Reserva> listar(@RequestParam(required = false) Long clienteId) {
        return clienteId == null ? reservaServicio.listar() : reservaServicio.listarDeCliente(clienteId);
    }

    // Dos formas de reservar (la URL ya exige RESERVA:CREAR o RESERVA:ALQUILAR):
    //  - un vendedor registra reservas para cualquier cliente, siempre a su propio nombre;
    //  - un cliente reserva solo para si mismo y sin vendedor (reserva online).
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reserva crear(@Valid @RequestBody NuevaReserva datos) {
        boolean esVendedor = usuarioActual.puede(Recurso.RESERVA, Accion.CREAR)
                && usuarioActual.puede(Recurso.RESERVA, Accion.VENDER);
        if (esVendedor) {
            if (!usuarioActual.esEl(datos.vendedorId())) {
                throw new AccesoDenegadoExcepcion("El vendedor de la reserva tiene que ser quien la registra");
            }
        } else if (!usuarioActual.esEl(datos.clienteId()) || datos.vendedorId() != null) {
            throw new AccesoDenegadoExcepcion("Solo podes reservar para vos mismo");
        }
        return reservaServicio.crear(datos);
    }

    // POST /api/reservas/5/cancelar (la URL ya exige RESERVA:EDITAR o RESERVA:ALQUILAR):
    // con RESERVA:EDITAR se cancela cualquiera; el cliente, solo las suyas
    @PostMapping("/{id}/cancelar")
    public Reserva cancelar(@PathVariable Long id) {
        if (!usuarioActual.puede(Recurso.RESERVA, Accion.EDITAR)) {
            Reserva reserva = reservaServicio.buscar(id);
            if (!usuarioActual.esEl(reserva.getCliente().getId())) {
                throw new AccesoDenegadoExcepcion("Solo podes cancelar tus propias reservas");
            }
        }
        return reservaServicio.cancelar(id);
    }

    @PostMapping("/{id}/devolver")
    public Reserva devolver(@PathVariable Long id) {
        return reservaServicio.devolver(id);
    }
}
