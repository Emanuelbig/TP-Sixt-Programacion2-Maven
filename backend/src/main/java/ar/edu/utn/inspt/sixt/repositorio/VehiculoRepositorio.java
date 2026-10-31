package ar.edu.utn.inspt.sixt.repositorio;

import ar.edu.utn.inspt.sixt.modelo.Vehiculo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehiculoRepositorio extends JpaRepository<Vehiculo, Long> {

    List<Vehiculo> findByFechaBajaIsNull();

    boolean existsByPatenteIgnoreCase(String patente);

    boolean existsByPatenteIgnoreCaseAndIdNot(String patente, Long id);
}
