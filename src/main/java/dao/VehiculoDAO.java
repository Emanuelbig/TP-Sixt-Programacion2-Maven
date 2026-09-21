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
import modelos.Auto;
import modelos.Camioneta;
import modelos.Oficina;
import modelos.Vehiculo;

/**
 *
 * @author Ian
 */
public class VehiculoDAO implements DAO<Vehiculo> {

    private final Path RUTA_VEHICULOS = Paths.get("data/vehiculos.txt");
    private final List<Oficina> oficinas;

    public VehiculoDAO(List<Oficina> oficinas) {
        this.oficinas = oficinas;
    }

    // ================= SECCIÓN VEHÍCULOS =================
    // Necesita la lista de oficinas ya cargada, para reconstruir oficinaActual
    @Override
    public void guardar(List<Vehiculo> vehiculos) {
        List<String> lineas = new ArrayList<>();
        for (Vehiculo v : vehiculos) {
            lineas.add(v.toCSV());
        }
        try {
            Files.write(RUTA_VEHICULOS, lineas);
        } catch (IOException e) {
            System.out.println("Error al guardar vehiculos: " + e.getMessage());
        }
    }

    @Override
    public List<Vehiculo> leer() {
        List<Vehiculo> vehiculos = new ArrayList<>();

        if (!Files.exists(RUTA_VEHICULOS)) {
            return vehiculos;
        }

        try {
            List<String> lineas = Files.readAllLines(RUTA_VEHICULOS);
            for (String linea : lineas) {
                String[] d = linea.split(",");
                int id = Integer.parseInt(d[0]);
                String tipo = d[1];
                String patente = d[2];
                String marca = d[3];
                String modelo = d[4];
                String color = d[5];
                double precioBaseDiario = Double.parseDouble(d[6]);
                int idOficina = Integer.parseInt(d[7]);
                Oficina oficina = buscarOficinaPorId(idOficina);
                if (tipo.equals("AUTO")) {

                    vehiculos.add(
                            new Auto(
                                    id,
                                    patente,
                                    marca,
                                    modelo,
                                    color,
                                    precioBaseDiario,
                                    oficina
                            )
                    );

                } else if (tipo.equals("CAMIONETA")) {

                    double recargoCapacidad = Double.parseDouble(d[8]);

                    vehiculos.add(
                            new Camioneta(
                                    id,
                                    patente,
                                    marca,
                                    modelo,
                                    color,
                                    precioBaseDiario,
                                    oficina,
                                    recargoCapacidad
                            )
                    );
                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer vehiculos: " + e.getMessage());
        }
        return vehiculos;
    }

    public int siguienteIdVehiculo(List<Vehiculo> vehiculos) {
        int max = 0;
        for (Vehiculo v : vehiculos) {
            if (v.getId() > max) {
                max = v.getId();
            }
        }
        return max + 1;
    }

    private Oficina buscarOficinaPorId(int id) {
        for (Oficina oficina : oficinas) {
            if (oficina.getIdOficina() == id) {
                return oficina;
            }
        }
        return null;
    }

}
