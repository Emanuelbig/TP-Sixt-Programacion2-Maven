package com.sixt.servicio;

import com.sixt.dto.NuevaReserva;
import com.sixt.dto.NuevoDetalleReserva;
import com.sixt.modelo.DetalleReserva;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import com.sixt.excepcion.ConflictoExcepcion;
import com.sixt.excepcion.DatosInvalidosExcepcion;
import com.sixt.excepcion.RecursoNoEncontradoExcepcion;
import com.sixt.modelo.Permiso;
import com.sixt.modelo.Persona;
import com.sixt.modelo.Reserva;
import com.sixt.modelo.Vehiculo;
import com.sixt.repositorio.ReservaRepositorio;
import com.sixt.servicio.precio.CalculadoraPrecio;
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

        if (datos.detalles() == null || datos.detalles().isEmpty()) {
            throw new DatosInvalidosExcepcion("La reserva debe incluir al menos un vehiculo");
        }
        Set<Long> ids = new HashSet<>();
        List<DetalleReserva> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (NuevoDetalleReserva pedido : datos.detalles()) {
            if (pedido == null || pedido.vehiculoId() == null || pedido.litrosNafta() == null
                    || pedido.litrosNafta() < 0) {
                throw new DatosInvalidosExcepcion("Cada detalle debe indicar vehiculo y litros no negativos");
            }
            if (!ids.add(pedido.vehiculoId())) {
                throw new DatosInvalidosExcepcion("Un vehiculo no puede repetirse en la misma reserva");
            }
            Vehiculo vehiculo = vehiculoServicio.buscarActivo(pedido.vehiculoId());
            if (reservaRepositorio.vehiculoOcupadoEntre(vehiculo.getId(), fechaDesde, fechaHasta)) {
                throw new ConflictoExcepcion("El vehiculo " + vehiculo.getPatente()
                        + " no esta disponible entre el " + fechaDesde + " y el " + fechaHasta);
            }
            DetalleReserva detalle = new DetalleReserva();
            detalle.setVehiculo(vehiculo);
            detalle.setPatente(vehiculo.getPatente());
            detalle.setColor(vehiculo.getColor());
            detalle.setAnio(vehiculo.getAnio());
            detalle.setLitrosNafta(pedido.litrosNafta());
            detalle.setPrecioAlquiler(calculadoraPrecio.calcular(vehiculo, fechaDesde, fechaHasta));
            total = total.add(detalle.getPrecioAlquiler());
            detalles.add(detalle);
        }

        Persona cliente = personaServicio.buscarActiva(datos.clienteId());
        Persona vendedor = personaServicio.buscarActiva(datos.vendedorId());
        exigirPermiso(cliente, Permiso.ALQUILAR, "cliente");
        exigirPermiso(vendedor, Permiso.VENDER, "vendedor");

        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setVendedor(vendedor);
        detalles.forEach(reserva::agregarDetalle);
        reserva.setOficinaOrigen(oficinaServicio.buscar(datos.oficinaOrigenId()));
        reserva.setOficinaDestino(oficinaServicio.buscar(datos.oficinaDestinoId()));
        reserva.setFechaDesde(fechaDesde);
        reserva.setFechaHasta(fechaHasta);
        reserva.setPrecioTotal(total);
        reserva.setDevuelto(false);
        return reservaRepositorio.save(reserva);
    }

    // La devolucion es conjunta: todos los vehiculos quedan en la oficina de destino
    @Transactional
    public Reserva devolver(Long id) {
        Reserva reserva = reservaRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("la reserva", id));
        if (Boolean.TRUE.equals(reserva.getDevuelto())) {
            return reserva;
        }
        reserva.setDevuelto(true);
        reserva.getDetalles().forEach(detalle ->
                detalle.getVehiculo().setOficina(reserva.getOficinaDestino()));
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
