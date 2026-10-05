package ar.edu.utn.inspt.sixt.repository;

import ar.edu.utn.inspt.sixt.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
