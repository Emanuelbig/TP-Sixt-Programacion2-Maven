package com.sixt.repositorio;

import com.sixt.modelo.Modelo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModeloRepositorio extends JpaRepository<Modelo, Long> {
}
