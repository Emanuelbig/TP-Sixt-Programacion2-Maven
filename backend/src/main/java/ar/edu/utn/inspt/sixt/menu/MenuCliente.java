/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ar.edu.utn.inspt.sixt.menu;

import ar.edu.utn.inspt.sixt.dto.UsuarioDTO;
import java.util.List;
import java.util.Scanner;
import ar.edu.utn.inspt.sixt.servicios.SixtServicio;

/**
 *
 * @author Ian
 */
public class MenuCliente {

    private final Scanner scanner;
    private final SixtServicio servicio;
    private final UsuarioDTO usuario;

    public MenuCliente(
            Scanner scanner,
            SixtServicio servicio,
            UsuarioDTO usuario) {

        this.scanner = scanner;
        this.servicio = servicio;
        this.usuario = usuario;
    }

    void mostrar() {
        int opcion;
        do {
            System.out.println("\n--- MENU CLIENTE ---");
            System.out.println("1. Ver mis reservas");
            System.out.println("2. Ver mi estado/deuda");
            System.out.println("3. Cerrar sesion");
            //System.out.print("Elegí una opción: ");
            //opcion = Integer.parseInt(scanner.nextLine());

            opcion = pedirEntero(scanner, "Elegi una opcion: ");

            switch (opcion) {
                case 1:
                    verMisReservas(servicio, usuario);
                    pausar(scanner);
                    break;
                case 2:
                    verMiInfo(usuario);
                    pausar(scanner);
                    break;
                case 3:
                    System.out.println("Cerrando sesion...");
                    break;
                default:
                    System.out.println("Opción invalida");
                    break;
            }
        } while (opcion != 3);
    }

    static void verMisReservas(ar.edu.utn.inspt.sixt.servicios.SixtServicio servicio, ar.edu.utn.inspt.sixt.dto.UsuarioDTO usuario) {
        ar.edu.utn.inspt.sixt.dto.ClienteDTO cliente = (ar.edu.utn.inspt.sixt.dto.ClienteDTO) usuario;
        List<ar.edu.utn.inspt.sixt.modelos.Reserva> misReservas = servicio.getReservasPorCliente(cliente.getId());

        if (misReservas.isEmpty()) {
            System.out.println("No tenes reservas registradas.");
        } else {
            System.out.println("--- TUS RESERVAS ---");
            for (ar.edu.utn.inspt.sixt.modelos.Reserva r : misReservas) {
                System.out.println(r);
            }
        }
    }

    static void verMiInfo(ar.edu.utn.inspt.sixt.dto.UsuarioDTO usuario) {
        ar.edu.utn.inspt.sixt.dto.ClienteDTO cliente = (ar.edu.utn.inspt.sixt.dto.ClienteDTO) usuario;
        System.out.println("--- TU INFORMACION ---");
        System.out.println("ID: " + cliente.getId());
        System.out.println("Usuario: " + cliente.getUsername());
        System.out.println("Nombre: " + cliente.getNombre());
        System.out.println("DNI: " + cliente.getDni());
    }

    static int pedirEntero(Scanner scanner, String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Error: Por favor ingresa un numero entero valido.");
            }
        }
    }

    static void pausar(Scanner scanner) {
        System.out.println("\nPresiona ENTER para volver al menu...");
        scanner.nextLine();
    }

}
