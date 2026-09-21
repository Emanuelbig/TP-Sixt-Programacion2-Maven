package modelos;

public class Auto extends Vehiculo {

    public Auto(int id, String patente, String marca, String modelo, String color, double precioBaseDiario, Oficina oficinaActual) {
        super(id, patente, marca, modelo, color, precioBaseDiario, oficinaActual);
    }

    @Override
    public double calcularAlquiler(int dias) {
        return precioBaseDiario * dias;
    }

    @Override
    public String toCSV() {
        // Formato: id;TIPO;patente;marca;modelo;color;precio;idOficina
        return id + ",AUTO," + patente + "," + marca + "," + modelo + "," + color + "," + precioBaseDiario + "," + oficinaActual.getIdOficina();
    }

    @Override
    public String toString() {
        return "Auto [" + patente + "] " + marca + " " + modelo;
    }
}