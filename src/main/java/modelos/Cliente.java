package modelos;

public class Cliente extends Usuario {

    public Cliente(int id, String username, String password, String dni,
            String nombre, String direccion, String email, String telefono) {
        super(id, username, password, dni, nombre, direccion, email, telefono);
    }

    @Override
    public String getRol() {
        return "CLIENTE";
    }

}
