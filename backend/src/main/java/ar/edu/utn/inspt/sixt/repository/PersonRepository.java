package ar.edu.utn.inspt.sixt.repository;

import ar.edu.utn.inspt.sixt.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
