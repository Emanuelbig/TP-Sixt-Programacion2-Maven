package com.sixt;

import com.sixt.dto.NuevaReserva;
import com.sixt.dto.NuevoDetalleReserva;
import com.sixt.modelo.Reserva;
import com.sixt.modelo.Vehiculo;
import com.sixt.modelo.Permiso;
import com.sixt.repositorio.*;
import com.sixt.servicio.ReservaServicio;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:detalles-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa", "spring.datasource.password="})
@Transactional
class DetalleReservaPersistenciaTest {
    @Autowired ReservaServicio servicio;
    @Autowired ReservaRepositorio reservas;
    @Autowired VehiculoRepositorio vehiculos;
    @Autowired PersonaRepositorio personas;
    @Autowired OficinaRepositorio oficinas;
    @Autowired EntityManager em;
    @Autowired tools.jackson.databind.ObjectMapper json;

    @Test
    void persisteDetallesConsultaDisponibilidadYDevuelveTodos() throws Exception {
        List<Vehiculo> autos = vehiculos.findAll();
        var cliente = personas.findAll().stream().filter(p -> p.puede(Permiso.ALQUILAR)).findFirst().orElseThrow();
        var vendedor = personas.findAll().stream().filter(p -> p.puede(Permiso.VENDER)).findFirst().orElseThrow();
        var origen = oficinas.findAll().getFirst();
        var destino = oficinas.findAll().getLast();
        LocalDate desde = LocalDate.now().plusDays(5);
        LocalDate hasta = desde.plusDays(2);
        Reserva creada = servicio.crear(new NuevaReserva(cliente.getId(), vendedor.getId(),
                origen.getId(), destino.getId(), desde, hasta,
                List.of(new NuevoDetalleReserva(autos.get(0).getId(), 20),
                        new NuevoDetalleReserva(autos.get(1).getId(), 35))));
        Long id = creada.getId();
        BigDecimal total = creada.getPrecioTotal();
        em.flush();
        em.clear();
        Reserva leida = reservas.findById(id).orElseThrow();
        assertThat(leida.getDetalles()).hasSize(2);
        var respuesta = json.readTree(json.writeValueAsString(leida));
        assertThat(respuesta.get("detalles").size()).isEqualTo(2);
        assertThat(respuesta.get("detalles").get(0).has("reserva")).isFalse();
        assertThat(leida.getDetalles().stream().map(d -> d.getPrecioAlquiler()).reduce(BigDecimal.ZERO, BigDecimal::add))
                .isEqualByComparingTo(total);
        for (var detalle : leida.getDetalles()) {
            Long vehiculoId = detalle.getVehiculo().getId();
            assertThat(reservas.vehiculoOcupadoEntre(vehiculoId, desde, hasta)).isTrue();
            assertThat(reservas.vehiculoOcupadoEntre(vehiculoId, hasta.plusDays(1), hasta.plusDays(2))).isFalse();
            assertThat(reservas.existsByDetallesVehiculoIdAndDevueltoFalseAndFechaCancelacionIsNull(vehiculoId)).isTrue();
        }
        servicio.devolver(id);
        em.flush();
        em.clear();
        Reserva devuelta = reservas.findById(id).orElseThrow();
        assertThat(devuelta.getDevuelto()).isTrue();
        for (var detalle : devuelta.getDetalles()) {
            assertThat(detalle.getVehiculo().getOficina().getId()).isEqualTo(destino.getId());
            assertThat(reservas.vehiculoOcupadoEntre(detalle.getVehiculo().getId(), desde, hasta)).isFalse();
            assertThat(reservas.existsByDetallesVehiculoIdAndDevueltoFalseAndFechaCancelacionIsNull(detalle.getVehiculo().getId())).isFalse();
        }
    }
}
