/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.util.List;

/**
*
* Grupo H, Programacion 2, Turno Noche, Año 2026
* 
**/
public class ReservaDTO {

    //Atributos
    private String idReserva;
    private String clienteDni;
    private List<String> patentesInvolucradas;
    private double precioTotal;
    private boolean estadoEntregado;

    public ReservaDTO(String idReserva, String clienteDni, List<String> patentesInvolucradas, double precioTotal, boolean estadoEntregado) {
        this.idReserva = idReserva;
        this.clienteDni = clienteDni;
        this.patentesInvolucradas = patentesInvolucradas;
        this.precioTotal = precioTotal;
        this.estadoEntregado = estadoEntregado;
    }

    public String getIdReserva() {
        return idReserva;
    }

    public String getClienteDni() {
        return clienteDni;
    }

    public List<String> getPatentesInvolucradas() {
        return patentesInvolucradas;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public boolean isEstadoEntregado() {
        return estadoEntregado;
    }
}
