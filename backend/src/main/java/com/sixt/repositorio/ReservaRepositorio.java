package com.sixt.repositorio;

import com.sixt.modelo.Reserva;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservaRepositorio extends JpaRepository<Reserva, Long> {

    List<Reserva> findByClienteId(Long clienteId);

    boolean existsByVehiculoIdAndDevueltoFalse(Long vehiculoId);

    boolean existsByClienteIdAndDevueltoFalse(Long clienteId);

    // Dos rangos de fechas se pisan si cada uno empieza antes de que termine el otro.
    // Las reservas ya devueltas no cuentan: el auto esta libre.
    @Query("""
            select count(r) > 0 from Reserva r
            where r.vehiculo.id = :vehiculoId
              and r.devuelto = false
              and r.fechaDesde <= :fechaHasta
              and r.fechaHasta >= :fechaDesde
            """)
    boolean vehiculoOcupadoEntre(@Param("vehiculoId") Long vehiculoId,
                                 @Param("fechaDesde") LocalDate fechaDesde,
                                 @Param("fechaHasta") LocalDate fechaHasta);
}
