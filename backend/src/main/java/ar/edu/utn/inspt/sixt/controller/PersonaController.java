package ar.edu.utn.inspt.sixt.controller;

import ar.edu.utn.inspt.sixt.entity.Person;
import ar.edu.utn.inspt.sixt.entity.Role;
import ar.edu.utn.inspt.sixt.repository.PersonRepository;
import ar.edu.utn.inspt.sixt.repository.RoleRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PersonaController {

    private final PersonRepository personRepository;
    private final RoleRepository roleRepository;

    public record PersonaRequest(int dni, String name, String email, int phone, Long roleId) {}

    @GetMapping("/personas")
    public List<Person> listar() {
        return personRepository.findAll();
    }

    @GetMapping("/roles")
    public List<Role> roles() {
        return roleRepository.findAll();
    }

    @PostMapping("/personas")
    public Person crear(@RequestBody PersonaRequest request) {
        Person person = new Person();
        person.setDni(request.dni());
        person.setName(request.name());
        person.setEmail(request.email());
        person.setPhone(request.phone());
        person.setRole(roleRepository.findById(request.roleId()).orElseThrow());
        person.setCreatedAt(Instant.now());
        return personRepository.save(person);
    }
}
