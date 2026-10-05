package ar.edu.utn.inspt.sixt.modelos;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "oficinas")
public class Oficina {

    @Id
    private int idOficina;
    private String nombre;
    private String direccion;

    protected Oficina() {
    }

    public Oficina(int idOficina, String nombre, String direccion) {
        this.idOficina = idOficina;
        this.nombre = nombre;
        this.direccion = direccion;
    }

    // Getters
    public int getIdOficina() {
        return idOficina;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    // Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    // Nota: no hay setIdOficina(), mismo criterio que en Usuario y Vehiculo.

    public String toCSV() {
        return idOficina + "," + nombre + "," + direccion;
    }

    @Override
    public String toString() {
        return "Oficina " + nombre + " (" + idOficina + ")";
    }
}