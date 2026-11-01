package com.sixt.repositorio;

import com.sixt.modelo.Persona;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaRepositorio extends JpaRepository<Persona, Long> {

    List<Persona> findByFechaBajaIsNull();

    List<Persona> findByRolNombreIgnoreCaseAndFechaBajaIsNull(String nombre);

    Optional<Persona> findByEmailIgnoreCase(String email);

    boolean existsByDni(int dni);

    boolean existsByDniAndIdNot(int dni, Long id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
