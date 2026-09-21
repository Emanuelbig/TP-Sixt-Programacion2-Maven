package modelos;

public class Camioneta extends Vehiculo {

    private double recargoCapacidad;

    public Camioneta(int id, String patente, String marca, String modelo, String color, double precioBaseDiario, Oficina oficinaActual, double recargoCapacidad) {
        super(id, patente, marca, modelo, color, precioBaseDiario, oficinaActual);
        this.recargoCapacidad = recargoCapacidad;
    }

    public double getRecargoCapacidad() {
        return recargoCapacidad;
    }

    public void setRecargoCapacidad(double recargoCapacidad) {
        this.recargoCapacidad = recargoCapacidad;
    }

    @Override
    public double calcularAlquiler(int dias) {
        return (precioBaseDiario * dias) + recargoCapacidad;
    }

    @Override
    public String toCSV() {
        // Formato: id;TIPO;patente;marca;modelo;color;precio;idOficina;recargo
        return id + ",CAMIONETA," + patente + "," + marca + "," + modelo + "," + color + "," + precioBaseDiario + "," + oficinaActual.getIdOficina() + "," + recargoCapacidad;
    }

    @Override
    public String toString() {
        return "Camioneta [" + patente + "] " + marca + " " + modelo;
    }
}