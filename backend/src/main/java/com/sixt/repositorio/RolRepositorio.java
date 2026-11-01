package com.sixt.repositorio;

import com.sixt.modelo.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepositorio extends JpaRepository<Rol, Long> {

    boolean existsByNombreIgnoreCase(String nombre);
}
