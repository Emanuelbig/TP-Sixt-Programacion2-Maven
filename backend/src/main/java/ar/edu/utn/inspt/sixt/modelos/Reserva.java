package ar.edu.utn.inspt.sixt.modelos;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    private int idReserva;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente clienteTitular;
    @ManyToMany
    @JoinTable(name = "reserva_vehiculos",
            joinColumns = @JoinColumn(name = "reserva_id"),
            inverseJoinColumns = @JoinColumn(name = "vehiculo_id"))
    private List<Vehiculo> vehiculosAlquilados;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oficina_origen_id")
    private Oficina oficinaOrigen;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oficina_destino_id")
    private Oficina oficinaDestino;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private double litrosGasolinaInicial;
    private double precioTotal;
    private boolean entregado;

    protected Reserva() {
    }

    public Reserva(int idReserva, Cliente clienteTitular, List<Vehiculo> vehiculosAlquilados, Oficina oficinaOrigen, Oficina oficinaDestino, LocalDate fechaInicio, LocalDate fechaFin, double litrosGasolinaInicial, double precioTotal, boolean entregado) {
        this.idReserva = idReserva;
        this.clienteTitular = clienteTitular;
        this.vehiculosAlquilados = vehiculosAlquilados;
        this.oficinaOrigen = oficinaOrigen;
        this.oficinaDestino = oficinaDestino;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.litrosGasolinaInicial = litrosGasolinaInicial;
        this.precioTotal = precioTotal;
        this.entregado = entregado;
    }

    // Getters
    public int getIdReserva() {
        return idReserva;
    }

    public Cliente getClienteTitular() {
        return clienteTitular;
    }

    public List<Vehiculo> getVehiculosAlquilados() {
        return vehiculosAlquilados;
    }

    public Oficina getOficinaOrigen() {
        return oficinaOrigen;
    }

    public Oficina getOficinaDestino() {
        return oficinaDestino;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public double getLitrosGasolinaInicial() {
        return litrosGasolinaInicial;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public boolean isEntregado() {
        return entregado;
    }

    // Setters
    public void setClienteTitular(Cliente clienteTitular) {
        this.clienteTitular = clienteTitular;
    }

    public void setVehiculosAlquilados(List<Vehiculo> vehiculosAlquilados) {
        this.vehiculosAlquilados = vehiculosAlquilados;
    }

    public void setOficinaOrigen(Oficina oficinaOrigen) {
        this.oficinaOrigen = oficinaOrigen;
    }

    public void setOficinaDestino(Oficina oficinaDestino) {
        this.oficinaDestino = oficinaDestino;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public void setLitrosGasolinaInicial(double litrosGasolinaInicial) {
        this.litrosGasolinaInicial = litrosGasolinaInicial;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public void setEntregado(boolean entregado) {
        this.entregado = entregado;
    }
    // Nota: no hay setIdReserva(), mismo criterio que en los otros modelos.

    public String toCSV() {
        // Ahora referenciamos por ID en vez de DNI/patente
        StringBuilder idsVehiculos = new StringBuilder();
        for (int i = 0; i < vehiculosAlquilados.size(); i++) {
            idsVehiculos.append(vehiculosAlquilados.get(i).getId());
            if (i < vehiculosAlquilados.size() - 1) {
                idsVehiculos.append(",");
            }
        }

        // Formato: id;idCliente;idsVehiculos;idOficinaOrigen;idOficinaDestino;fechaInicio;fechaFin;litros;precioTotal;entregado
        return idReserva + ","
                + clienteTitular.getId() + ","
                + idsVehiculos.toString() + ","
                + oficinaOrigen.getIdOficina() + ","
                + oficinaDestino.getIdOficina() + ","
                + fechaInicio.toString() + ","
                + fechaFin.toString() + ","
                + litrosGasolinaInicial + ","
                + precioTotal + ","
                + entregado;
    }

    @Override
    public String toString() {
        String estado = entregado ? "FINALIZADA" : "EN CURSO";
        return "Reserva #" + idReserva + " | Cliente: " + clienteTitular.getNombre() + " | Estado: " + estado;
    }
}
