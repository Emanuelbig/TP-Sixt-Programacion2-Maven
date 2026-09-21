/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicios;

import dao.OficinaDAO;
import dao.ReservaDAO;
import dao.UsuarioDAO;
import dao.VehiculoDAO;
import dto.UsuarioDTO;
import java.time.LocalDate;
import java.util.List;
import modelos.Usuario;
import modelos.Oficina;
import modelos.Vehiculo;
import modelos.Reserva;

/**
 *
 *
 */
public class SixtServicio {

    //private final UsuarioServicio usuarioServicio;
    // Declaramos conexiones a las otras capas
    private final UsuarioDAO usuarioDao;
    private final OficinaDAO oficinaDAO;
    private VehiculoDAO vehiculoDAO;
    private ReservaDAO reservaDAO;

    private final AuthServicios AuthServicios;
    private UsuarioServicio usuarioServicio;
    private OficinaServicio oficinaServicio;
    private VehiculoServicio vehiculoServicio;
    private ReservaServicio reservaServicio;
    
    // Lista que esta en la ram mientras usamos el programa
    private List<Usuario> usuarios;
    private List<Oficina> oficinas;
    private List<Vehiculo> vehiculos;
    private List<Reserva> reservas;

    public SixtServicio() {
        this.usuarioDao = new UsuarioDAO();
        this.oficinaDAO = new OficinaDAO();
        //No inicializar en el constructor, sino en cargarDatos...
        //this.vehiculoDAO = new VehiculoDAO();
        this.AuthServicios = new AuthServicios();

        // para cargar el txt en la memoria
        cargarDatosEnMemoria();
    }

    //aca esta el metodo que carga desarrollado
    private void cargarDatosEnMemoria() {
        this.oficinas = oficinaDAO.leer();
        this.usuarios = usuarioDao.leer();
        if (this.usuarios.isEmpty()) {
            int idAdmin = usuarioDao.siguienteId(this.usuarios);
            modelos.Administrador adminRoot = new modelos.Administrador(idAdmin, "admin", "1234",
                    "00000000", "Administrador inicial", "Casa central", "admin@sixt.com.ar", "1100000000");
            this.usuarios.add(adminRoot);
            usuarioDao.guardar(this.usuarios);
            System.out.println("Sistema inicializado: Se ha creado el usuario 'admin' con clave '1234'.");
        }
        this.vehiculoDAO = new VehiculoDAO(this.oficinas);
        this.vehiculos = vehiculoDAO.leer();
        this.reservaDAO = new ReservaDAO(this.usuarios, this.vehiculos, this.oficinas);
        this.reservas = reservaDAO.leerReservas(usuarios, vehiculos, oficinas);
        this.usuarioServicio = new UsuarioServicio(
                usuarioDao,
                this.usuarios
        );
        this.oficinaServicio = new OficinaServicio(this.oficinas, this.oficinaDAO);
        this.vehiculoServicio = new VehiculoServicio(this.oficinas, this.vehiculos, this.vehiculoDAO, this.reservas);
        this.reservaServicio = new ReservaServicio(this.usuarios, this.oficinas,this.vehiculos, this.reservaDAO,this.vehiculoDAO, this.reservas, this.vehiculoServicio);
    }

    //metodo para iniciar sesion en el main
    public UsuarioDTO iniciarSesion(String username, String password) {
        return AuthServicios.login(username, password, this.usuarios);
    }

    //Todo esto se fue a UsuarioServicio y desde acá solo invocamos esa clase
    public boolean registrarNuevoCliente(
            String username,
            String password,
            String dni,
            String nombre,
            String direccion,
            String email,
            String telefono) {

        return usuarioServicio.registrarNuevoCliente(
                username,
                password,
                dni,
                nombre,
                direccion,
                email,
                telefono
        );
    }

    public boolean registrarNuevoVendedor(
            String username,
            String password,
            String dni,
            String nombre,
            String direccion,
            String email,
            String telefono) {

        return usuarioServicio.registrarNuevoVendedor(
                username,
                password,
                dni,
                nombre,
                direccion,
                email,
                telefono
        );
    }

    public boolean registrarNuevoAdministrador(
            String username,
            String password,
            String dni,
            String nombre,
            String direccion,
            String email,
            String telefono) {

        return usuarioServicio.registrarNuevoAdministrador(
                username,
                password,
                dni,
                nombre,
                direccion,
                email,
                telefono
        );
    }

    //esto se fue a oficinaServicio y ahora SixtServicio solo lo invoca
    public boolean registrarNuevaOficina(String nombre, String direccion) {
        return oficinaServicio.registrarNuevaOficina(nombre, direccion);
    }

    public List<Oficina> getOficinas() {
        return oficinaServicio.getOficinas();
    }

    //esto se va a VehiculoServicio
    public boolean registrarNuevoVehiculo(String tipo, String patente, String marca, String modelo, String color, double precioBaseDiario, int idOficina, double recargoCapacidad) {
        return vehiculoServicio.registrarNuevoVehiculo(tipo, patente, marca, modelo, color, precioBaseDiario, idOficina, recargoCapacidad);
    }

    public List<Vehiculo> getVehiculosDisponibles(LocalDate fechaInicio, LocalDate fechaFin) {
        return vehiculoServicio.getVehiculosDisponibles(fechaInicio, fechaFin);
    }

    //esto se va a ReservaServicio
    public boolean registrarNuevaReserva(String dniCliente, List<Integer> idsVehiculos, LocalDate fechaInicio, LocalDate fechaFin, int idOficinaDestino, double litrosGasolinaInicial) {
        return reservaServicio.registrarNuevaReserva(dniCliente, idsVehiculos, fechaInicio, fechaFin, idOficinaDestino, litrosGasolinaInicial);
    }
    
    public boolean marcarReservaComoEntregada(int idReserva) {
        return reservaServicio.marcarReservaComoEntregada(idReserva);
    }

    public List<Reserva> getReservasPendientes() {        
        return reservaServicio.getReservasPendientes();
    }

    public List<Reserva> getReservasPorCliente(int idCliente) {
        return reservaServicio.getReservasPorCliente(idCliente);
    }

    public List<Vehiculo> getVehiculos() {
        return vehiculos;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }
}
