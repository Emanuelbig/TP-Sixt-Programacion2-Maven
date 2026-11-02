package com.sixt.repositorio;

import com.sixt.modelo.Persona;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// Las busquedas por email pasaron a UsuarioRepositorio (app seguridad)
public interface PersonaRepositorio extends JpaRepository<Persona, Long> {

    List<Persona> findByFechaBajaIsNull();

    boolean existsByDni(int dni);

    boolean existsByDniAndIdNot(int dni, Long id);

    long countByFechaBajaIsNull();
}
