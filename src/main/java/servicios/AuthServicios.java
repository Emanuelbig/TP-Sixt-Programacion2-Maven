/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicios;

import dto.ClienteDTO;
import dto.UsuarioDTO;
import java.util.List;
import modelos.Cliente;
import modelos.Usuario;

/**
 * Esta clase se encarga de verificar si usuario y contraseña estan en la lista y si estan devolver un dto limpio, para que el main entienda quien inicia sesion.
 *
 */
public class AuthServicios {

    public UsuarioDTO login(String username, String password, List<Usuario> usuariosEnMemoria) {
        for (Usuario u : usuariosEnMemoria) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return mapeoUsuarioDto(u);
            }
        }
        return null; // Si esta mal las credenciales (user y pass)
    }

    
    private UsuarioDTO mapeoUsuarioDto(Usuario u) {
        // Si el usuario es un Cliente, usamos el Cliente dto
        if (u.getRol().equals("CLIENTE")) {
            Cliente c = (Cliente) u;
            return new ClienteDTO(c.getUsername(), c.getRol(), c.getDni(), c.getNombre());
        }

        // Si es Admin o Vendedor, usamos usuario dto
        return new UsuarioDTO(u.getUsername(), u.getRol());
    }
}
