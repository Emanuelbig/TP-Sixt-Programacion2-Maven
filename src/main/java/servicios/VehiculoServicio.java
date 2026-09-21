/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicios;

import dao.VehiculoDAO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import modelos.Auto;
import modelos.Camioneta;
import modelos.Oficina;
import modelos.Reserva;
import modelos.Vehiculo;

/**
 *
 * @author Ian
 */
public class VehiculoServicio {

    private final List<Oficina> oficinas;
    private final List<Vehiculo> vehiculos;
    private final VehiculoDAO vehiculoDAO;
    private final List<Reserva> reservas;

    public VehiculoServicio(
            List<Oficina> oficinas,
            List<Vehiculo> vehiculos,
            VehiculoDAO vehiculoDAO,
            List<Reserva> reservas) {

        this.oficinas = oficinas;
        this.vehiculos = vehiculos;
        this.vehiculoDAO = vehiculoDAO;
        this.reservas = reservas;
    }

    public boolean registrarNuevoVehiculo(String tipo, String patente, String marca, String modelo, String color, double precioBaseDiario, int idOficina, double recargoCapacidad) {
        // Validar que exista al menos una oficina
        if (oficinas.isEmpty()) {
            return false;
        }

        // Buscar la oficina por id
        Oficina oficinaElegida = null;
        for (Oficina o : oficinas) {
            if (o.getIdOficina() == idOficina) {
                oficinaElegida = o;
                break;
            }
        }
        if (oficinaElegida == null) {
            return false;
        }

        // Validar que la patente no esté repetida
        for (Vehiculo v : vehiculos) {
            if (v.getPatente().equalsIgnoreCase(patente)) {
                return false;
            }
        }

        int id = vehiculoDAO.siguienteIdVehiculo(this.vehiculos);
        Vehiculo nuevoVehiculo;

        if (tipo.equals("AUTO")) {
            nuevoVehiculo = new Auto(id, patente, marca, modelo, color, precioBaseDiario, oficinaElegida);
        } else {
            nuevoVehiculo = new Camioneta(id, patente, marca, modelo, color, precioBaseDiario, oficinaElegida, recargoCapacidad);
        }

        this.vehiculos.add(nuevoVehiculo);
        vehiculoDAO.guardar(this.vehiculos);
        return true;
    }

    public List<Vehiculo> getVehiculosDisponibles(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Vehiculo> disponibles = new ArrayList<>();

        for (Vehiculo v : vehiculos) {
            boolean ocupado = false;

            for (Reserva r : reservas) {
                if (r.getVehiculosAlquilados().contains(v)) {
                    boolean seSolapan = !(fechaInicio.isAfter(r.getFechaFin()) || fechaFin.isBefore(r.getFechaInicio()));
                    if (seSolapan) {
                        ocupado = true;
                        break;
                    }
                }
            }

            if (!ocupado) {
                disponibles.add(v);
            }
        }
        return disponibles;
    }

}
