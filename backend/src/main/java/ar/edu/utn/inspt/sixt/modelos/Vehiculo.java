package ar.edu.utn.inspt.sixt.modelos;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehiculos")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_vehiculo")
public abstract class Vehiculo {

    @Id
    protected int id;
    protected String patente;
    protected String marca;
    protected String modelo;
    protected String color;
    protected double precioBaseDiario;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oficina_actual_id")
    protected Oficina oficinaActual;

    protected Vehiculo() {
    }

    public Vehiculo(int id, String patente, String marca, String modelo, String color, double precioBaseDiario, Oficina oficinaActual) {
        this.id = id;
        this.patente = patente;
        this.marca = marca;
        this.modelo = modelo;
        this.color = color;
        this.precioBaseDiario = precioBaseDiario;
        this.oficinaActual = oficinaActual;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getPatente() {
        return patente;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public String getColor() {
        return color;
    }

    public double getPrecioBaseDiario() {
        return precioBaseDiario;
    }

    public Oficina getOficinaActual() {
        return oficinaActual;
    }

    // Setters
    public void setPatente(String patente) {
        this.patente = patente;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setPrecioBaseDiario(double precioBaseDiario) {
        this.precioBaseDiario = precioBaseDiario;
    }

    public void setOficinaActual(Oficina oficinaActual) {
        this.oficinaActual = oficinaActual;
    }
    // Nota: no hay setId(), mismo criterio que en Usuario.

    public abstract double calcularAlquiler(int dias);
    public abstract String toCSV();
}