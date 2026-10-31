package ar.edu.utn.inspt.sixt.servicio;

import ar.edu.utn.inspt.sixt.dto.NuevaReserva;
import ar.edu.utn.inspt.sixt.excepcion.ConflictoExcepcion;
import ar.edu.utn.inspt.sixt.excepcion.DatosInvalidosExcepcion;
import ar.edu.utn.inspt.sixt.excepcion.RecursoNoEncontradoExcepcion;
import ar.edu.utn.inspt.sixt.modelo.Permiso;
import ar.edu.utn.inspt.sixt.modelo.Persona;
import ar.edu.utn.inspt.sixt.modelo.Reserva;
import ar.edu.utn.inspt.sixt.modelo.Vehiculo;
import ar.edu.utn.inspt.sixt.repositorio.ReservaRepositorio;
import ar.edu.utn.inspt.sixt.servicio.precio.CalculadoraPrecio;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservaServicio {

    private final ReservaRepositorio reservaRepositorio;
    private final PersonaServicio personaServicio;
    private final VehiculoServicio vehiculoServicio;
    private final OficinaServicio oficinaServicio;
    private final CalculadoraPrecio calculadoraPrecio;

    public List<Reserva> listar() {
        return reservaRepositorio.findAll();
    }

    public List<Reserva> listarDeCliente(Long clienteId) {
        return reservaRepositorio.findByClienteId(clienteId);
    }

    @Transactional
    public Reserva crear(NuevaReserva datos) {
        LocalDate fechaDesde = datos.fechaDesde();
        LocalDate fechaHasta = datos.fechaHasta();

        if (fechaHasta.isBefore(fechaDesde)) {
            throw new DatosInvalidosExcepcion("La fecha de fin no puede ser anterior a la de inicio");
        }

        Vehiculo vehiculo = vehiculoServicio.buscarActivo(datos.vehiculoId());

        if (reservaRepositorio.vehiculoOcupadoEntre(vehiculo.getId(), fechaDesde, fechaHasta)) {
            throw new ConflictoExcepcion("El vehiculo " + vehiculo.getPatente()
                    + " no esta disponible entre el " + fechaDesde + " y el " + fechaHasta);
        }

        Persona cliente = personaServicio.buscarActiva(datos.clienteId());
        Persona vendedor = personaServicio.buscarActiva(datos.vendedorId());
        exigirPermiso(cliente, Permiso.ALQUILAR, "cliente");
        exigirPermiso(vendedor, Permiso.VENDER, "vendedor");

        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setVendedor(vendedor);
        reserva.setVehiculo(vehiculo);
        reserva.setPatente(vehiculo.getPatente());
        reserva.setColor(vehiculo.getColor());
        reserva.setAnio(vehiculo.getAnio());
        reserva.setOficinaOrigen(oficinaServicio.buscar(datos.oficinaOrigenId()));
        reserva.setOficinaDestino(oficinaServicio.buscar(datos.oficinaDestinoId()));
        reserva.setFechaDesde(fechaDesde);
        reserva.setFechaHasta(fechaHasta);
        reserva.setLitrosNafta(datos.litrosNafta());
        reserva.setPrecioTotal(calculadoraPrecio.calcular(vehiculo, fechaDesde, fechaHasta));
        reserva.setDevuelto(false);
        return reservaRepositorio.save(reserva);
    }

    // Al devolver el auto, queda en la oficina de destino
    @Transactional
    public Reserva devolver(Long id) {
        Reserva reserva = reservaRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("la reserva", id));
        reserva.setDevuelto(true);
        reserva.getVehiculo().setOficina(reserva.getOficinaDestino());
        return reservaRepositorio.save(reserva);
    }

    private void exigirPermiso(Persona persona, Permiso permiso, String papel) {
        if (!persona.puede(permiso)) {
            throw new DatosInvalidosExcepcion(persona.getNombre() + " no puede figurar como " + papel
                    + " de una reserva: su rol " + persona.getRol().getNombre()
                    + " no tiene el permiso " + permiso);
        }
    }
}
