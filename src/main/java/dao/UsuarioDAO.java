/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import modelos.Administrador;
import modelos.Cliente;
import modelos.Usuario;
import modelos.Vendedor;

public class UsuarioDAO implements DAO<Usuario> {

    private final Path RUTA_USUARIOS =
            Paths.get("data/usuarios.txt");

    @Override
    public void guardar(List<Usuario> usuarios) {
        List<String> lineas = new ArrayList<>();

        for (Usuario u : usuarios) {
            lineas.add(u.toCSV());
        }

        try {
            Files.write(RUTA_USUARIOS, lineas);
        } catch (IOException e) {
            System.out.println(
                "Error critico al guardar los usuarios: "
                + e.getMessage()
            );
        }
    }

    @Override
    public List<Usuario> leer() {
        List<Usuario> usuarios = new ArrayList<>();

        if (!Files.exists(RUTA_USUARIOS)) {
            return usuarios;
        }

        try {
            List<String> lineas = Files.readAllLines(RUTA_USUARIOS);

            for (String linea : lineas) {
                String[] datos = linea.split(",");

                int id = Integer.parseInt(datos[0]);
                String rol = datos[1];
                String username = datos[2];
                String password = datos[3];
                String dni = datos[4];
                String nombre = datos[5];
                String direccion = datos[6];
                String email = datos[7];
                String telefono = datos[8];

                if (rol.equals("ADMIN")) {
                    usuarios.add(new Administrador(
                        id, username, password, dni, nombre,
                        direccion, email, telefono
                    ));
                } else if (rol.equals("VENDEDOR")) {
                    usuarios.add(new Vendedor(
                        id, username, password, dni, nombre,
                        direccion, email, telefono
                    ));
                } else if (rol.equals("CLIENTE")) {
                    usuarios.add(new Cliente(
                        id, username, password, dni, nombre,
                        direccion, email, telefono
                    ));
                }
            }

        } catch (IOException e) {
            System.out.println(
                "Error al leer el archivo de usuarios: "
                + e.getMessage()
            );
        }

        return usuarios;
    }

    public int siguienteId(List<Usuario> usuarios) {
        int max = 0;

        for (Usuario u : usuarios) {
            if (u.getId() > max) {
                max = u.getId();
            }
        }
        return max + 1;
    }
}