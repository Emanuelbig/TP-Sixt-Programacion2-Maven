package com.sixt.repositorio;

import com.sixt.modelo.TipoModelo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoModeloRepositorio extends JpaRepository<TipoModelo, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
