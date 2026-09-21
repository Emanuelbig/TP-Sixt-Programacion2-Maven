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
import modelos.Oficina;

/**
 *
 * @author Ian
 */
public class OficinaDAO implements DAO<Oficina> {

    private final Path RUTA_OFICINAS = Paths.get("data/oficinas.txt");
    // ================= SECCIÓN OFICINAS =================

    @Override
    public void guardar(List<Oficina> oficinas) {
        List<String> lineas = new ArrayList<>();
        for (Oficina o : oficinas) {
            lineas.add(o.toCSV());
        }
        try {
            Files.write(RUTA_OFICINAS, lineas);
        } catch (IOException e) {
            System.out.println("Error al guardar oficinas: " + e.getMessage());
        }
    }

    @Override
    public List<Oficina> leer() {
        List<Oficina> oficinas = new ArrayList<>();
        if (!Files.exists(RUTA_OFICINAS)) {
            return oficinas;
        }
        try {
            List<String> lineas = Files.readAllLines(RUTA_OFICINAS);
            for (String linea : lineas) {
                // Formato: id;nombre;direccion
                String[] d = linea.split(",");
                int id = Integer.parseInt(d[0]);
                oficinas.add(new Oficina(id, d[1], d[2]));
            }
        } catch (IOException e) {
            System.out.println("Error al leer oficinas: " + e.getMessage());
        }
        return oficinas;
    }

    public int siguienteIdOficina(List<Oficina> oficinas) {
        int max = 0;
        for (Oficina o : oficinas) {
            if (o.getIdOficina() > max) {
                max = o.getIdOficina();
            }
        }
        return max + 1;
    }
}
