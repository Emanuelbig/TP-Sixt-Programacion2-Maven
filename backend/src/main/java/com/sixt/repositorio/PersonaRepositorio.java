package com.sixt.repositorio;

import com.sixt.modelo.Permiso;
import com.sixt.modelo.Persona;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonaRepositorio extends JpaRepository<Persona, Long> {

    List<Persona> findByFechaBajaIsNull();

    List<Persona> findByRolNombreIgnoreCaseAndFechaBajaIsNull(String nombre);

    Optional<Persona> findByEmailIgnoreCase(String email);

    boolean existsByDni(int dni);

    boolean existsByDniAndIdNot(int dni, Long id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    // Se cuentan tambien las dadas de baja: siguen apuntando al rol
    boolean existsByRolId(Long rolId);

    long countByFechaBajaIsNull();

    // Alguien activo, con un rol distinto a rolId, que tiene ese permiso
    @Query("""
            select count(p) > 0 from Persona p join p.rol r join r.permisos permiso
            where permiso = :permiso and r.id <> :rolId and p.fechaBaja is null
            """)
    boolean existeOtroActivoConPermiso(@Param("permiso") Permiso permiso, @Param("rolId") Long rolId);
}
