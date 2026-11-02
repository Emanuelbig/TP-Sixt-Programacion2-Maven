package com.sixt;

import static org.assertj.core.api.Assertions.assertThat;

import com.sixt.modelo.DetalleReserva;
import com.sixt.modelo.Oficina;
import com.sixt.modelo.Reserva;
import com.sixt.repositorio.OficinaRepositorio;
import com.sixt.repositorio.PersonaRepositorio;
import com.sixt.repositorio.ReservaRepositorio;
import com.sixt.repositorio.VehiculoRepositorio;
import com.sixt.servicio.UbicacionDeFlota;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

// Los datos de ejemplo que se cargan al levantar la aplicacion con la base vacia
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:carga-inicial;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa", "spring.datasource.password=", "spring.jpa.show-sql=false"})
@Transactional
class CargaDatosInicialesTest {

    @Autowired PersonaRepositorio personas;
    @Autowired OficinaRepositorio oficinas;
    @Autowired VehiculoRepositorio vehiculos;
    @Autowired ReservaRepositorio reservas;
    @Autowired UbicacionDeFlota ubicacion;

    @Test
    void cargaPersonasOficinasFlotaYReservas() {
        assertThat(personas.count()).isEqualTo(8);
        List<String> ciudades = new ArrayList<>();
        for (Oficina oficina : oficinas.findAll()) {
            ciudades.add(oficina.getZona());
        }
        assertThat(ciudades).containsExactly("Buenos Aires", "Córdoba", "Rosario", "Mendoza");
        assertThat(vehiculos.count()).isEqualTo(6);
        assertThat(reservas.count()).isEqualTo(11);
    }

    @Test
    void cadaReservaPendienteSaleDeLaOficinaDondeVaAEstarElAuto() {
        int pendientes = 0;
        for (Reserva reserva : reservas.findAll()) {
            if (Boolean.TRUE.equals(reserva.getDevuelto()) || reserva.getFechaCancelacion() != null) {
                continue;
            }
            pendientes++;
            for (DetalleReserva detalle : reserva.getDetalles()) {
                Oficina dondeEstara = ubicacion.oficinaEn(detalle.getVehiculo(), reserva.getFechaDesde());
                assertThat(dondeEstara.getId()).as(detalle.getPatente()).isEqualTo(reserva.getOficinaOrigen().getId());
            }
        }
        assertThat(pendientes).isEqualTo(2);
    }
}
