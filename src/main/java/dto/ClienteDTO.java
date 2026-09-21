package dto;

/**
*
** Grupo H, Programacion 2, Turno Noche, Año 2026
* 
**/
public class ClienteDTO extends UsuarioDTO {

    private int id;
    private String dni;
    private String nombre;

    public ClienteDTO(String username, String rol, String dni, String nombre) {
        super(username, rol);
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }
}