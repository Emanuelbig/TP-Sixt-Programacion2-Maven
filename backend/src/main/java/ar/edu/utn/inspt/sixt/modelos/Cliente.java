package ar.edu.utn.inspt.sixt.modelos;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CLIENTE")
public class Cliente extends Usuario {

    protected Cliente() {
    }

    public Cliente(int id, String username, String password, String dni,
            String nombre, String direccion, String email, String telefono) {
        super(id, username, password, dni, nombre, direccion, email, telefono);
    }

    @Override
    public String getRol() {
        return "CLIENTE";
    }

}
