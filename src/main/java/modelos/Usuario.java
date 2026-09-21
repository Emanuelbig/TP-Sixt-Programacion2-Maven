package modelos;

public abstract class Usuario {

    // private, no protected: las hijas ya no necesitan tocarlos directo,
    // porque los metodos que los usan ahora viven aca.
    private int id;
    private String username;
    private String password;
    private String dni;
    private String nombre;
    private String direccion;
    private String email;
    private String telefono;

    //constructor
    public Usuario(int id, String username, String password, String dni,
            String nombre, String direccion, String email, String telefono) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.dni = dni;
        this.nombre = nombre;
        this.direccion = direccion;
        this.email = email;
        this.telefono = telefono;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    // Sin setId(): el id se define una sola vez, como una PK.

    /**
     * Lo unico que cambia segun el tipo de usuario.
     */
    public abstract String getRol();

    /**
     * Ahora es CONCRETO y esta escrito UNA sola vez. Antes las tres hijas lo
     * repetian, y en Administrador y Vendedor era identico.
     */
    public String toCSV() {
        return id + "," + getRol() + "," + username + "," + password + ","
                + dni + "," + nombre + "," + direccion + "," + email + "," + telefono;
    }

    @Override
    public String toString() {
        return "#" + id + " " + nombre + " | " + getRol() + " | DNI: " + dni;
    }
}
