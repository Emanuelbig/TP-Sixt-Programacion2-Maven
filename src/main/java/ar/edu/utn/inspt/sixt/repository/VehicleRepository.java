package ar.edu.utn.inspt.sixt.repository;

import ar.edu.utn.inspt.sixt.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
}
