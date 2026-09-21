/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicios;

import dao.ReservaDAO;
import dao.VehiculoDAO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import modelos.Cliente;
import modelos.Oficina;
import modelos.Reserva;
import modelos.Usuario;
import modelos.Vehiculo;

/**
 *
 * @author Ian
 */
public class ReservaServicio {

    private final List<Usuario> usuarios;
    private final List<Oficina> oficinas;
    private final List<Vehiculo> vehiculos;
    private final ReservaDAO reservaDAO;
    private final VehiculoDAO vehiculoDAO;
    private final List<Reserva> reservas;
    private final VehiculoServicio vehiculoServicio;

    public ReservaServicio(List<Usuario> usuarios, List<Oficina> oficinas,List<Vehiculo> vehiculos, ReservaDAO reservaDAO,VehiculoDAO vehiculoDAO, List<Reserva> reservas, VehiculoServicio vehiculoServicio) {
        this.usuarios = usuarios;
        this.oficinas = oficinas;
        this.vehiculos = vehiculos;
        this.reservaDAO = reservaDAO;
        this.vehiculoDAO = vehiculoDAO;
        this.reservas = reservas;
        this.vehiculoServicio = vehiculoServicio;
    }

    public boolean registrarNuevaReserva(String dniCliente, List<Integer> idsVehiculos, LocalDate fechaInicio, LocalDate fechaFin, int idOficinaDestino, double litrosGasolinaInicial) {

        // Buscar cliente por DNI
        Cliente cliente = null;
        for (Usuario u : usuarios) {
            if (u instanceof Cliente c && c.getDni().equals(dniCliente)) {
                cliente = c;
                break;
            }
        }
        if (cliente == null) {
            return false;
        }

        // Buscar oficina destino
        Oficina oficinaDestino = null;
        for (Oficina o : oficinas) {
            if (o.getIdOficina() == idOficinaDestino) {
                oficinaDestino = o;
                break;
            }
        }
        if (oficinaDestino == null) {
            return false;
        }

        // Buscar los vehículos elegidos y validar que sigan disponibles
        List<Vehiculo> disponibles = vehiculoServicio.getVehiculosDisponibles(fechaInicio, fechaFin);
        List<Vehiculo> vehiculosElegidos = new ArrayList<>();

        for (int idVehiculo : idsVehiculos) {
            Vehiculo encontrado = null;
            for (Vehiculo v : disponibles) {
                if (v.getId() == idVehiculo) {
                    encontrado = v;
                    break;
                }
            }
            if (encontrado == null) {
                return false; // El vehículo no existe o ya no está disponible
            }
            vehiculosElegidos.add(encontrado);
        }

        if (vehiculosElegidos.isEmpty()) {
            return false;
        }

        // La oficina de origen es la del primer vehículo elegido
        Oficina oficinaOrigen = vehiculosElegidos.get(0).getOficinaActual();

        // Calcular precio total
        long dias = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        double precioTotal = 0;
        for (Vehiculo v : vehiculosElegidos) {
            precioTotal += v.calcularAlquiler((int) dias);
        }

        int id = reservaDAO.siguienteId(this.reservas);
        Reserva nuevaReserva = new Reserva(id, cliente, vehiculosElegidos, oficinaOrigen, oficinaDestino,
                fechaInicio, fechaFin, litrosGasolinaInicial, precioTotal, false);

        this.reservas.add(nuevaReserva);
        reservaDAO.guardar(this.reservas);
        return true;
    }

    public boolean marcarReservaComoEntregada(int idReserva) {
        Reserva reservaEncontrada = null;
        for (Reserva r : reservas) {
            if (r.getIdReserva() == idReserva) {
                reservaEncontrada = r;
                break;
            }
        }

        if (reservaEncontrada == null || reservaEncontrada.isEntregado()) {
            return false; // No existe, o ya estaba entregada
        }

        reservaEncontrada.setEntregado(true);

        // Actualizamos la ubicación actual de cada vehículo a la oficina destino
        for (Vehiculo v : reservaEncontrada.getVehiculosAlquilados()) {
            v.setOficinaActual(reservaEncontrada.getOficinaDestino());
        }
        reservaDAO.guardar(this.reservas);
        vehiculoDAO.guardar(this.vehiculos); // persistimos la nueva ubicación de los vehículos
        return true;
    }

    public List<Reserva> getReservasPendientes() {
        List<Reserva> pendientes = new ArrayList<>();
        for (Reserva r : reservas) {
            if (!r.isEntregado()) {
                pendientes.add(r);
            }
        }
        return pendientes;
    }

    public List<Reserva> getReservasPorCliente(int idCliente) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getClienteTitular().getId() == idCliente) {
                resultado.add(r);
            }
        }
        return resultado;
    }

}
