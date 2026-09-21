/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

/**
*
* Grupo H, Programacion 2, Turno Noche, Año 2026
* 
**/
public class UsuarioDTO {

    //Atributos
    protected String username;
    protected String rol;

    //constructor
    public UsuarioDTO(String username, String rol) {
        this.username = username;
        this.rol = rol;
    }

    //getters
    public String getUsername() {
        return username;
    }

    public String getRol() {
        return rol;
    }
}
