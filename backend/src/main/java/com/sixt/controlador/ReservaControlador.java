package com.sixt.controlador;

import com.sixt.dto.NuevaReserva;
import com.sixt.dto.UsuarioLogueado;
import com.sixt.modelo.Permiso;
import com.sixt.excepcion.AccesoDenegadoExcepcion;
import jakarta.servlet.http.HttpServletRequest;
import com.sixt.modelo.Reserva;
import com.sixt.servicio.ReservaServicio;
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
    public List<Reserva> listar(@RequestParam(required = false) Long clienteId, HttpServletRequest request) {
        var usuario = (UsuarioLogueado) request.getAttribute("usuarioAutenticado");
        if (!usuario.permisos().contains(Permiso.VENDER)) {
            if (clienteId != null && !clienteId.equals(usuario.id())) throw new AccesoDenegadoExcepcion();
            return reservaServicio.listarDeCliente(usuario.id());
        }
        return clienteId == null ? reservaServicio.listar() : reservaServicio.listarDeCliente(clienteId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reserva crear(@Valid @RequestBody NuevaReserva datos, HttpServletRequest request) {
        var usuario = (UsuarioLogueado) request.getAttribute("usuarioAutenticado");
        if (usuario.permisos().contains(Permiso.VENDER)) {
            // El vendedor reserva para cualquier cliente, pero siempre a su propio nombre
            if (!usuario.id().equals(datos.vendedorId())) throw new AccesoDenegadoExcepcion();
        } else {
            // El cliente solo puede reservar para si mismo, y sin vendedor (reserva online)
            if (!usuario.id().equals(datos.clienteId()) || datos.vendedorId() != null) {
                throw new AccesoDenegadoExcepcion();
            }
        }
        return reservaServicio.crear(datos);
    }

    // POST /api/reservas/5/cancelar -> el vendedor cancela cualquiera, el cliente solo las suyas
    @PostMapping("/{id}/cancelar")
    public Reserva cancelar(@PathVariable Long id, HttpServletRequest request) {
        var usuario = (UsuarioLogueado) request.getAttribute("usuarioAutenticado");
        if (!usuario.permisos().contains(Permiso.VENDER)) {
            Reserva reserva = reservaServicio.buscar(id);
            if (!reserva.getCliente().getId().equals(usuario.id())) throw new AccesoDenegadoExcepcion();
        }
        return reservaServicio.cancelar(id);
    }

    @PostMapping("/{id}/devolver")
    public Reserva devolver(@PathVariable Long id) {
        return reservaServicio.devolver(id);
    }
}
