package ar.edu.utn.inspt.sixt.repository;

import ar.edu.utn.inspt.sixt.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelRepository extends JpaRepository<Model, Long> {
}
