package com.sixt.repositorio;

import com.sixt.modelo.Oficina;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OficinaRepositorio extends JpaRepository<Oficina, Long> {

    List<Oficina> findByFechaBajaIsNull();

    long countByFechaBajaIsNull();
}
