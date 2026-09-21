/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package menu;

import java.util.List;
import java.util.Scanner;
import servicios.SixtServicio;

/**
 *
 * @author Ian
 */
public class MenuAdmin {

    private final Scanner scanner;
    private final SixtServicio servicio;

    public MenuAdmin(Scanner scanner, SixtServicio servicio) {
        this.scanner = scanner;
        this.servicio = servicio;
    }

    void mostrar() {
        int opcion;
        do {
            System.out.println("\n--- MENU ADMINISTRADOR ---");
            System.out.println("1. Agregar oficina");
            System.out.println("2. Gestionar vehiculos");
            System.out.println("3. Agregar usuarios");
            System.out.println("4. Ver reservas");
            System.out.println("5. Ver vehiculos");
            System.out.println("6. Cerrar sesion");
            //System.out.print("Elegí una opción: ");
            opcion = pedirEntero(scanner, "Elegi una opcion: ");
            //opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1:
                    agregarOficina(scanner, servicio);
                    pausar(scanner);
                    break;
                case 2:
                    agregarVehiculo(scanner, servicio);
                    pausar(scanner);
                    break;
                case 3:
                    agregarUsuario(scanner, servicio);
                    pausar(scanner);
                    break;
                case 4:
                    listarReservas(servicio);
                    pausar(scanner);
                    break;
                case 5:
                    listarVehiculos(servicio);
                    pausar(scanner);
                    break;
                case 6:
                    System.out.println("Cerrando sesion...");
                    break;
                default:
                    System.out.println("Opcion inválida");
                    break;
            }
        } while (opcion != 6);
    }

    //para problemas con el teclado
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

    static void agregarUsuario(Scanner scanner, servicios.SixtServicio servicio) {
        System.out.println("1. Vendedor");
        System.out.println("2. Administrador");
        int tipoOpcion = pedirEntero(scanner, "Tipo de usuario: ");

        System.out.print("Nombre de usuario: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("DNI: ");
        String dni = scanner.nextLine();
        System.out.print("Nombre completo: ");
        String nombre = scanner.nextLine();
        System.out.print("Direccion: ");
        String direccion = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Telefono: ");
        String telefono = scanner.nextLine();

        boolean exito;
        if (tipoOpcion == 1) {
            exito = servicio.registrarNuevoVendedor(username, password, dni, nombre, direccion, email, telefono);
        } else {
            exito = servicio.registrarNuevoAdministrador(username, password, dni, nombre, direccion, email, telefono);
        }

        if (exito) {
            System.out.println("Usuario registrado correctamente.");
        } else {
            System.out.println("Error: ya existe un usuario con ese nombre.");
        }
    }

    static void agregarOficina(Scanner scanner, servicios.SixtServicio servicio) {
        System.out.print("Nombre de la oficina: ");
        String nombre = scanner.nextLine();
        System.out.print("Direccion: ");
        String direccion = scanner.nextLine();

        boolean exito = servicio.registrarNuevaOficina(nombre, direccion);

        if (exito) {
            System.out.println("Oficina agregada correctamente.");
        } else {
            System.out.println("Error: ya existe una oficina con ese nombre.");
        }
    }

    static void agregarVehiculo(Scanner scanner, servicios.SixtServicio servicio) {
        if (servicio.getOficinas().isEmpty()) {
            System.out.println("No hay oficinas cargadas. Primero carga una oficina para poder dar de alta un vehiculo.");
            return;
        }

        System.out.println("1. Auto");
        System.out.println("2. Camioneta");
        System.out.print("Tipo de vehiculo: ");
        int tipoOpcion = Integer.parseInt(scanner.nextLine());
        String tipo = (tipoOpcion == 1) ? "AUTO" : "CAMIONETA";

        System.out.print("Patente: ");
        String patente = scanner.nextLine();
        System.out.print("Marca: ");
        String marca = scanner.nextLine();
        System.out.print("Modelo: ");
        String modelo = scanner.nextLine();
        System.out.print("Color: ");
        String color = scanner.nextLine();
        //System.out.print("Precio base diario: ");
        //double precio = Double.parseDouble(scanner.nextLine());
        double precio = pedirDouble(scanner, "Precio base diario: ");

        System.out.println("Oficinas disponibles:");
        for (modelos.Oficina o : servicio.getOficinas()) {
            System.out.println(o);
        }
        System.out.print("ID de oficina: ");
        int idOficina = Integer.parseInt(scanner.nextLine());

        double recargo = 0;
        if (tipoOpcion == 2) {
            System.out.print("Recargo por capacidad: ");
            recargo = Double.parseDouble(scanner.nextLine());
        }

        boolean exito = servicio.registrarNuevoVehiculo(tipo, patente, marca, modelo, color, precio, idOficina, recargo);

        if (exito) {
            System.out.println("Vehiculo agregado correctamente.");
        } else {
            System.out.println("Error: revise que la oficina exista y la patente no este repetida.");
        }
    }

    static void listarVehiculos(servicios.SixtServicio servicio) {
        List<modelos.Vehiculo> vehiculos = servicio.getVehiculos();
        if (vehiculos.isEmpty()) {
            System.out.println("No hay vehiculos cargados.");
        } else {
            System.out.println("--- VEHICULOS ---");
            for (modelos.Vehiculo v : vehiculos) {
                System.out.println(v);
            }
        }
    }

    static void listarReservas(servicios.SixtServicio servicio) {
        List<modelos.Reserva> reservas = servicio.getReservas();
        if (reservas.isEmpty()) {
            System.out.println("No hay reservas cargadas.");
        } else {
            System.out.println("--- RESERVAS ---");
            for (modelos.Reserva r : reservas) {
                System.out.println(r);
            }
        }
    }

    static double pedirDouble(Scanner scanner, String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Por favor ingresa un numero decimal valido (Ej: 10.5).");
            }
        }
    }

    static void pausar(Scanner scanner) {
        System.out.println("\nPresiona ENTER para volver al menu...");
        scanner.nextLine();
    }
}
