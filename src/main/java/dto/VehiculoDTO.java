/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

/**
*
* Grupo H, Programacion 2, Turno Noche, Año 2026
* 
**/
public class VehiculoDTO {

    //Atributos
    private String patente;
    private String marca;
    private String modelo;
    private double precioDiario;

    //Constructor
    public VehiculoDTO(String patente, String marca, String modelo, double precioDiario) {
        this.patente = patente;
        this.marca = marca;
        this.modelo = modelo;
        this.precioDiario = precioDiario;
    }

    //Getters
    public String getPatente() {
        return patente;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public double getPrecioDiario() {
        return precioDiario;
    }
}
