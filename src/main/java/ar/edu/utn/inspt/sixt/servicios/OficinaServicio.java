/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ar.edu.utn.inspt.sixt.servicios;

import ar.edu.utn.inspt.sixt.dao.OficinaDAO;
import java.util.List;
import ar.edu.utn.inspt.sixt.modelos.Oficina;

/**
 *
 * @author Ian
 */
public class OficinaServicio {
    private List<Oficina> oficinas;
    private final OficinaDAO oficinaDAO;

    public OficinaServicio(List<Oficina> oficinas, OficinaDAO oficinaDAO) {
        this.oficinas = oficinas;
        this.oficinaDAO = oficinaDAO;
    }
    
    public boolean registrarNuevaOficina(String nombre, String direccion) {
        // Verificamos que no exista ya una oficina con ese nombre (sin importar mayúsc/minúsc)
        for (Oficina o : oficinas) {
            if (o.getNombre().equalsIgnoreCase(nombre)) {
                return false; // Ya existe una oficina con ese nombre
            }
        }

        int id = oficinaDAO.siguienteIdOficina(this.oficinas);
        Oficina nuevaOficina = new Oficina(id, nombre, direccion);

        this.oficinas.add(nuevaOficina);
        oficinaDAO.guardar(this.oficinas);
        return true;
    }

    public List<Oficina> getOficinas() {
        return oficinas;
    }
    
}
