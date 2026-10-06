package ar.edu.utn.inspt.sixt.config;

import ar.edu.utn.inspt.sixt.entity.Model;
import ar.edu.utn.inspt.sixt.entity.ModelType;
import ar.edu.utn.inspt.sixt.entity.Office;
import ar.edu.utn.inspt.sixt.entity.Person;
import ar.edu.utn.inspt.sixt.entity.Role;
import ar.edu.utn.inspt.sixt.entity.Vehicle;
import ar.edu.utn.inspt.sixt.repository.ModelRepository;
import ar.edu.utn.inspt.sixt.repository.ModelTypeRepository;
import ar.edu.utn.inspt.sixt.repository.OfficeRepository;
import ar.edu.utn.inspt.sixt.repository.PersonRepository;
import ar.edu.utn.inspt.sixt.repository.RoleRepository;
import ar.edu.utn.inspt.sixt.repository.VehicleRepository;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// Carga datos de ejemplo la primera vez (si la base esta vacia),
// tomados de los .txt viejos de la carpeta data/.
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PersonRepository personRepository;
    private final OfficeRepository officeRepository;
    private final ModelTypeRepository modelTypeRepository;
    private final ModelRepository modelRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.count() > 0) {
            return;
        }

        Role admin = role("ADMIN");
        Role vendedor = role("VENDEDOR");
        Role cliente = role("CLIENTE");

        person(1, "Administrador inicial", "admin@sixt.com.ar", 1100000000, admin);
        person(30111222, "Pepe Vendedor", "pepe@sixt.com.ar", 1133445566, vendedor);
        person(38258469, "Ema Morano", "ema@gmail.com", 1133168094, cliente);

        Office ofi1 = office("Ofi 1", "Corrientes y Callao");
        office("Ofi 2", "Acoyte 721");
        office("Acoyte y Rivadavia", "Acoyte 19");

        ModelType auto = modelType("AUTO", "10000");
        ModelType camioneta = modelType("CAMIONETA", "18000");

        Model p206 = model("206", "Peugeot", auto);
        Model hilux = model("Hilux", "Toyota", camioneta);

        vehicle("APG023", "violeta", 2010, p206, ofi1);
        vehicle("AE123CD", "blanco", 2022, hilux, ofi1);
    }

    private Role role(String name) {
        Role r = new Role();
        r.setName(name);
        return roleRepository.save(r);
    }

    private void person(int dni, String name, String email, int phone, Role role) {
        Person p = new Person();
        p.setDni(dni);
        p.setName(name);
        p.setEmail(email);
        p.setPhone(phone);
        p.setRole(role);
        p.setCreatedAt(Instant.now());
        personRepository.save(p);
    }

    private Office office(String zone, String address) {
        Office o = new Office();
        o.setZone(zone);
        o.setAddress(address);
        return officeRepository.save(o);
    }

    private ModelType modelType(String name, String price) {
        ModelType t = new ModelType();
        t.setName(name);
        t.setPrice(new BigDecimal(price));
        return modelTypeRepository.save(t);
    }

    private Model model(String name, String brand, ModelType type) {
        Model m = new Model();
        m.setName(name);
        m.setBrand(brand);
        m.setType(type);
        return modelRepository.save(m);
    }

    private void vehicle(String plate, String colour, int year, Model model, Office office) {
        Vehicle v = new Vehicle();
        v.setPlate(plate);
        v.setColour(colour);
        v.setYear(year);
        v.setModel(model);
        v.setOffice(office);
        v.setCreatedAt(Instant.now());
        vehicleRepository.save(v);
    }
}
