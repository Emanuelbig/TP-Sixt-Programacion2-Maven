package com.sixt.repositorio;

import com.sixt.modelo.Reserva;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservaRepositorio extends JpaRepository<Reserva, Long> {

    List<Reserva> findByClienteId(Long clienteId);

    // "Pendiente" = ni devuelta ni cancelada
    boolean existsByDetallesVehiculoIdAndDevueltoFalseAndFechaCancelacionIsNull(Long vehiculoId);

    boolean existsByClienteIdAndDevueltoFalseAndFechaCancelacionIsNull(Long clienteId);

    @Query("""
            select count(r) > 0 from Reserva r
            where r.devuelto = false and r.fechaCancelacion is null
              and (r.oficinaOrigen.id = :oficinaId or r.oficinaDestino.id = :oficinaId)
            """)
    boolean tieneReservasPendientesEnOficina(@Param("oficinaId") Long oficinaId);

    // Dos rangos de fechas se pisan si cada uno empieza antes de que termine el otro.
    // Las reservas ya devueltas o canceladas no cuentan: el auto esta libre.
    @Query("""
            select count(r) > 0 from Reserva r join r.detalles d
            where d.vehiculo.id = :vehiculoId
              and r.devuelto = false
              and r.fechaCancelacion is null
              and r.fechaDesde <= :fechaHasta
              and r.fechaHasta >= :fechaDesde
            """)
    boolean vehiculoOcupadoEntre(@Param("vehiculoId") Long vehiculoId,
                                 @Param("fechaDesde") LocalDate fechaDesde,
                                 @Param("fechaHasta") LocalDate fechaHasta);
}
