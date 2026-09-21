package dao;

/**
 *
 * @author Ian
 */
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import modelos.Cliente;
import modelos.Oficina;
import modelos.Reserva;
import modelos.Usuario;
import modelos.Vehiculo;

public class ReservaDAO implements DAO<Reserva> {

    private final Path RUTA_RESERVAS = Paths.get("data/reservas.txt");
    private final List<Usuario> usuarios;
    private final List<Vehiculo> vehiculos;
    private final List<Oficina> oficinas;

    public ReservaDAO(
            List<Usuario> usuarios,
            List<Vehiculo> vehiculos,
            List<Oficina> oficinas) {

        this.usuarios = usuarios;
        this.vehiculos = vehiculos;
        this.oficinas = oficinas;
    }

    @Override
    public void guardar(List<Reserva> reservas) {
        List<String> lineas = new ArrayList<>();
        for (Reserva r : reservas) {
            lineas.add(r.toCSV());
        }
        try {
            Files.write(RUTA_RESERVAS, lineas);
        } catch (IOException e) {
            System.out.println("Error al guardar reservas: " + e.getMessage());
        }
    }

    @Override
    public List<Reserva> leer() {
        return new ArrayList<>();
    }

    public List<Reserva> leerReservas(List<Usuario> usuarios, List<Vehiculo> vehiculos, List<Oficina> oficinas) {
        List<Reserva> reservas = new ArrayList<>();
        if (!Files.exists(RUTA_RESERVAS)) {
            return reservas;
        }
        try {
            List<String> lineas = Files.readAllLines(RUTA_RESERVAS);
            for (String linea : lineas) {
                // Formato: id;idCliente;idsVehiculos;idOficinaOrigen;idOficinaDestino;fechaInicio;fechaFin;litros;precioTotal;entregado
                String[] d = linea.split(",");

                int idReserva = Integer.parseInt(d[0]);
                int idCliente = Integer.parseInt(d[1]);
                Cliente cliente = buscarClientePorId(usuarios, idCliente);

                List<Vehiculo> vehiculosReserva = new ArrayList<>();
                for (String idTexto : d[2].split(",")) {
                    Vehiculo v = buscarVehiculoPorId(vehiculos, Integer.parseInt(idTexto));
                    if (v != null) {
                        vehiculosReserva.add(v);
                    }
                }

                int idOficinaOrigen = Integer.parseInt(d[3]);
                int idOficinaDestino = Integer.parseInt(d[4]);
                Oficina oOrigen = buscarOficinaPorId(oficinas, idOficinaOrigen);
                Oficina oDestino = buscarOficinaPorId(oficinas, idOficinaDestino);

                LocalDate fechaInicio = LocalDate.parse(d[5]);
                LocalDate fechaFin = LocalDate.parse(d[6]);
                double litros = Double.parseDouble(d[7]);
                double precioTotal = Double.parseDouble(d[8]);
                boolean entregado = Boolean.parseBoolean(d[9]);

                reservas.add(new Reserva(idReserva, cliente, vehiculosReserva, oOrigen, oDestino,
                        fechaInicio, fechaFin, litros, precioTotal, entregado));
            }
        } catch (IOException e) {
            System.out.println("Error al leer reservas: " + e.getMessage());
        }
        return reservas;
    }

    public int siguienteId(List<Reserva> reservas) {
        int max = 0;

        for (Reserva r : reservas) {
            if (r.getIdReserva() > max) {
                max = r.getIdReserva();
            }
        }

        return max + 1;
    }

    private Vehiculo buscarVehiculoPorId(List<Vehiculo> vehiculos, int id) {
        for (Vehiculo v : vehiculos) {
            if (v.getId() == id) {
                return v;
            }
        }
        return null;
    }

    private Oficina buscarOficinaPorId(List<Oficina> oficinas, int id) {
        for (Oficina o : oficinas) {
            if (o.getIdOficina() == id) {
                return o;
            }
        }
        return null;
    }

    private Cliente buscarClientePorId(List<Usuario> usuarios, int id) {
        for (Usuario u : usuarios) {
            if (u instanceof Cliente c && c.getId() == id) {
                return c;
            }
        }
        return null;
    }
}
