package ar.edu.utn.inspt.sixt.repositorio;

import ar.edu.utn.inspt.sixt.modelo.Modelo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModeloRepositorio extends JpaRepository<Modelo, Long> {
}
