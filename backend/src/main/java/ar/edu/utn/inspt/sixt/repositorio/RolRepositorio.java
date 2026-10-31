package ar.edu.utn.inspt.sixt.repositorio;

import ar.edu.utn.inspt.sixt.modelo.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepositorio extends JpaRepository<Rol, Long> {

    boolean existsByNombreIgnoreCase(String nombre);
}
