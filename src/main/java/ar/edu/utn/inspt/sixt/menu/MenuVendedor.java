/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ar.edu.utn.inspt.sixt.menu;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import ar.edu.utn.inspt.sixt.servicios.SixtServicio;

/**
 *
 * @author Ian
 */
public class MenuVendedor {

    private final Scanner scanner;
    private final SixtServicio servicio;
    static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public MenuVendedor(Scanner scanner, SixtServicio servicio) {
        this.scanner = scanner;
        this.servicio = servicio;
    }

    void mostrar() {
        int opcion;
        do {
            System.out.println("\n--- MENU VENDEDOR ---");
            System.out.println("1. Gestionar clientes");
            System.out.println("2. Crear reservas");
            System.out.println("3. Marcar reserva como entregada");
            System.out.println("4. Ver vehiculos disponibles");
            System.out.println("5. Cerrar sesion");
            //System.out.print("Elegí una opción: ");
            //opcion = Integer.parseInt(scanner.nextLine());
            opcion = pedirEntero(scanner, "Elegi una opcion: ");

            switch (opcion) {
                case 1:
                    agregarCliente(scanner, servicio);
                    pausar(scanner);
                    break;
                case 2:
                    agregarReserva(scanner, servicio);
                    pausar(scanner);
                    break;
                case 3:
                    marcarEntrega(scanner, servicio);
                    pausar(scanner);
                    break;
                case 4:
                    verDisponibles(scanner, servicio);
                    pausar(scanner);
                    break;
                case 5:
                    System.out.println("Cerrando sesion...");
                    break;
                default:
                    System.out.println("Opcion invalida");
                    break;
            }
        } while (opcion != 5);
    }

    static void verDisponibles(Scanner scanner, ar.edu.utn.inspt.sixt.servicios.SixtServicio servicio) {
        LocalDate fechaInicio, fechaFin;
        try {
            System.out.print("Fecha de inicio (DD/MM/YYYY): ");
            fechaInicio = LocalDate.parse(scanner.nextLine(), FORMATO_FECHA);
            System.out.print("Fecha de fin (DD/MM/YYYY): ");
            fechaFin = LocalDate.parse(scanner.nextLine(), FORMATO_FECHA);
        } catch (Exception e) {
            System.out.println("Fecha invalida. Usa el formato DD/MM/YYYY.");
            return;
        }

        List<ar.edu.utn.inspt.sixt.modelos.Vehiculo> disponibles = servicio.getVehiculosDisponibles(fechaInicio, fechaFin);
        if (disponibles.isEmpty()) {
            System.out.println("No hay vehiculos disponibles para ese rango de fechas.");
        } else {
            System.out.println("--- VEHICULOS DISPONIBLES ---");
            for (ar.edu.utn.inspt.sixt.modelos.Vehiculo v : disponibles) {
                System.out.println(v);
            }
        }
    }

    static void agregarCliente(Scanner scanner, ar.edu.utn.inspt.sixt.servicios.SixtServicio servicio) {
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

        boolean exito = servicio.registrarNuevoCliente(username, password, dni, nombre, direccion, email, telefono);

        if (exito) {
            System.out.println("Cliente registrado correctamente.");
        } else {
            System.out.println("Error: ya existe un usuario con ese nombre o un cliente con ese DNI.");
        }
    }

    static void agregarReserva(Scanner scanner, ar.edu.utn.inspt.sixt.servicios.SixtServicio servicio) {
        LocalDate fechaInicio, fechaFin;
        try {
            System.out.print("Fecha de inicio (DD/MM/YYYY): ");
            fechaInicio = LocalDate.parse(scanner.nextLine(), FORMATO_FECHA);
            System.out.print("Fecha de fin (DD/MM/YYYY): ");
            fechaFin = LocalDate.parse(scanner.nextLine(), FORMATO_FECHA);
        } catch (Exception e) {
            System.out.println("Fecha invalida. Usa el formato DD/MM/YYYY.");
            return;
        }

        if (fechaFin.isBefore(fechaInicio)) {
            System.out.println("La fecha de fin no puede ser anterior a la de inicio.");
            return;
        }

        List<ar.edu.utn.inspt.sixt.modelos.Vehiculo> disponibles = servicio.getVehiculosDisponibles(fechaInicio, fechaFin);
        if (disponibles.isEmpty()) {
            System.out.println("No hay vehiculos disponibles para ese rango de fechas.");
            return;
        }

        System.out.println("Vehiculos disponibles:");
        for (ar.edu.utn.inspt.sixt.modelos.Vehiculo v : disponibles) {
            //Mostramos el ID de forma bien clara antes del texto del auto
            System.out.println("ID: " + v.getId() + " - " + v.toString());
        }

        List<Integer> idsElegidos = new ArrayList<>();
        boolean seguirAgregando = true;
        while (seguirAgregando) {
            int idVehiculo = pedirEntero(scanner, "ID de vehiculo a agregar: ");

            //Verificamos que el ID tipeado exista realmente en la lista de disponibles
            boolean existe = false;
            for (ar.edu.utn.inspt.sixt.modelos.Vehiculo v : disponibles) {
                if (v.getId() == idVehiculo) {
                    existe = true;
                }
            }

            if (!existe) {
                System.out.println("--> ERROR: El ID ingresado no esta en la lista de disponibles.");
                continue; // Lo hace volver a pedir el ID
            }

            idsElegidos.add(idVehiculo);

            //Obligamos a que la respuesta sea estrictamente 's' o 'n'
            String respuesta = "";
            while (!respuesta.equalsIgnoreCase("s") && !respuesta.equalsIgnoreCase("n")) {
                System.out.print("Agregar otro vehiculo? (s/n): ");
                respuesta = scanner.nextLine();
            }
            seguirAgregando = respuesta.equalsIgnoreCase("s");
        }

        //Aviso preventivo para el usuario
        System.out.println("\n(Aviso: El cliente ya debe estar registrado en el sistema)");
        System.out.print("DNI del cliente: ");
        String dniCliente = scanner.nextLine();

        System.out.println("Oficinas disponibles:");
        for (ar.edu.utn.inspt.sixt.modelos.Oficina o : servicio.getOficinas()) {
            System.out.println(o); // Si acá también te faltan los IDs, cambialo a: System.out.println("ID: " + o.getIdOficina() + " - " + o.toString());
        }

        int idOficinaDestino = pedirEntero(scanner, "ID de oficina destino: ");
        double litros = pedirDouble(scanner, "Litros de gasolina inicial: ");

        boolean exito = servicio.registrarNuevaReserva(dniCliente, idsElegidos, fechaInicio, fechaFin, idOficinaDestino, litros);

        if (exito) {
            System.out.println("Reserva registrada correctamente.");
        } else {
            // MEJORA 5: Mensaje de error desglosado
            System.out.println("--> ERROR AL REGISTRAR RESERVA. Por favor revise:");
            System.out.println("  1. Que el DNI (" + dniCliente + ") pertenezca a un Cliente ya registrado.");
            System.out.println("  2. Que la oficina de destino exista.");
        }
    }

    static void marcarEntrega(Scanner scanner, ar.edu.utn.inspt.sixt.servicios.SixtServicio servicio) {
        List<ar.edu.utn.inspt.sixt.modelos.Reserva> pendientes = servicio.getReservasPendientes();

        if (pendientes.isEmpty()) {
            System.out.println("No hay reservas pendientes de entrega.");
            return;
        }

        System.out.println("Reservas pendientes:");
        for (ar.edu.utn.inspt.sixt.modelos.Reserva r : pendientes) {
            System.out.println(r);
        }

        System.out.print("ID de reserva a marcar como entregada: ");
        int idReserva = Integer.parseInt(scanner.nextLine());

        boolean exito = servicio.marcarReservaComoEntregada(idReserva);

        if (exito) {
            System.out.println("Reserva marcada como entregada. Los vehiculos ya estan disponibles en la oficina destino.");
        } else {
            System.out.println("Error: la reserva no existe o ya estaba entregada.");
        }
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
