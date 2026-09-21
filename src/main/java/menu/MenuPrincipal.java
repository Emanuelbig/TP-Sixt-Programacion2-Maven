/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package menu;

import dto.UsuarioDTO;
import java.util.Scanner;
import servicios.SixtServicio;
/**
 *
 * @author Ian
 */

public class MenuPrincipal {

    private final Scanner scanner;
    private final SixtServicio servicio;

    public MenuPrincipal(Scanner scanner, SixtServicio servicio) {
        this.scanner = scanner;
        this.servicio = servicio;
    }

    public void mostrar() {

        boolean salirDelPrograma = false;

        while (!salirDelPrograma) {

            System.out.println("BIENVENIDO A SIXT, ALQUILA TU AUTO :)");
            System.out.println("1. Iniciar sesion");
            System.out.println("2. Salir del sistema");

            int opcion = pedirEntero("Elegi una opcion: \n\n");

            switch (opcion) {

                case 1:
                    iniciarSesion();
                    break;

                case 2:
                    System.out.println(
                        "Guardando cambios, saliendo del sistema.."
                    );
                    salirDelPrograma = true;
                    break;

                default:
                    System.out.println("Opcion invalida.");
                    break;
            }
        }
    }

    private void iniciarSesion() {

        System.out.println("Usuario:");
        String user = scanner.nextLine();

        System.out.println("Password:");
        String pass = scanner.nextLine();

        UsuarioDTO usuarioLogueado = servicio.iniciarSesion(user, pass);

        if (usuarioLogueado == null) {

            System.out.println(
                "Error: Credenciales incorrectas. Intente nuevamente."
            );
            return;
        }

        System.out.println(
            "Login exitoso! Bienvenido, "
            + usuarioLogueado.getUsername()
        );

        switch (usuarioLogueado.getRol()) {

            case "ADMIN":
                new MenuAdmin(scanner, servicio).mostrar();
                break;

            case "VENDEDOR":
                new MenuVendedor(scanner, servicio).mostrar();
                break;

            case "CLIENTE":
                new MenuCliente(scanner, servicio, usuarioLogueado).mostrar();
                break;

            default:
                System.out.println("Rol desconocido.");
                break;
        }
    }

    private int pedirEntero(String mensaje) {

        while (true) {

            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine());

            } catch (NumberFormatException e) {

                System.out.println(
                    "Error: Por favor ingresa un numero entero valido."
                );
            }
        }
    }
}