/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicios;

import dao.UsuarioDAO;
import java.util.List;
import modelos.Administrador;
import modelos.Cliente;
import modelos.Usuario;
import modelos.Vendedor;

/**
 *
 * @author Ian
 */
public class UsuarioServicio {
    private List<Usuario> usuarios;
    private final UsuarioDAO usuarioDao;

    public UsuarioServicio(UsuarioDAO usuarioDao, List<Usuario> usuarios) {
        this.usuarios = usuarios;
        this.usuarioDao = usuarioDao;
    }

    public boolean registrarNuevoVendedor(String username, String password, String dni,
            String nombre, String direccion, String email, String telefono) {
        for (Usuario u : usuarios) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return false;
            }
        }

        int id = usuarioDao.siguienteId(this.usuarios);
        Vendedor nuevoVendedor = new Vendedor(id, username, password, dni, nombre, direccion, email, telefono);

        this.usuarios.add(nuevoVendedor);
        usuarioDao.guardar(this.usuarios);
        return true;
    }

    //metodo para registrar un nuevo cliente
    public boolean registrarNuevoCliente(String username, String password, String dni, String nombre, String direccion, String email, String telefono) {
        // Verificar que no exista el mismo username
        for (Usuario u : usuarios) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return false;
            }
        }
        // Verificar que no exista el mismo DNI
        for (Usuario u : usuarios) {
            if (u instanceof Cliente c && c.getDni().equals(dni)) {
                return false;
            }
        }

        int id = usuarioDao.siguienteId(this.usuarios);
        Cliente nuevoCliente = new Cliente(id, username, password, dni, nombre, direccion, email, telefono);

        this.usuarios.add(nuevoCliente);
        usuarioDao.guardar(this.usuarios);
        return true;
    }

    public boolean registrarNuevoAdministrador(String username, String password, String dni,
            String nombre, String direccion, String email, String telefono) {
        for (Usuario u : usuarios) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return false;
            }
        }

        int id = usuarioDao.siguienteId(this.usuarios);
        Administrador nuevoAdmin = new Administrador(id, username, password, dni, nombre, direccion, email, telefono);

        this.usuarios.add(nuevoAdmin);
        usuarioDao.guardar(this.usuarios);
        return true;
    }

}
