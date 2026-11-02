package com.sixt.configuracion;

import com.sixt.modelo.DetalleReserva;
import com.sixt.modelo.Modelo;
import com.sixt.modelo.Oficina;
import com.sixt.modelo.Persona;
import com.sixt.modelo.Reserva;
import com.sixt.modelo.TipoModelo;
import com.sixt.modelo.Vehiculo;
import com.sixt.repositorio.ModeloRepositorio;
import com.sixt.repositorio.OficinaRepositorio;
import com.sixt.repositorio.PersonaRepositorio;
import com.sixt.repositorio.ReservaRepositorio;
import com.sixt.repositorio.TipoModeloRepositorio;
import com.sixt.repositorio.VehiculoRepositorio;
import com.sixt.seguridad.servicio.RolServicio;
import com.sixt.seguridad.servicio.UsuarioServicio;
import com.sixt.servicio.precio.CalculadoraPrecio;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

// Carga datos de ejemplo la primera vez (si la base esta vacia). Corre despues de CargaRolesIniciales.
//
// Usuarios de prueba:
//   sysadmin@sixt.com.ar / sysadmin123   (SYSADMIN)
//   admin@sixt.com.ar    / admin123      (ADMIN)
//   pepe@sixt.com.ar     / vendedor123   (VENDEDOR)
//   lucia@sixt.com.ar    / vendedor123   (VENDEDOR)
//   ema@gmail.com, martin@gmail.com, sofia@gmail.com, juan@gmail.com / cliente123 (CLIENTE)
//
// Las fechas de las reservas se calculan desde el dia en que se levanta la aplicacion,
// asi los reportes del año en curso siempre tienen datos. El historial es coherente con
// UbicacionDeFlota: cada auto esta en la oficina donde se devolvio por ultima vez,
// y sus reservas futuras salen de ahi.
@Component
@Order(2)
@RequiredArgsConstructor
public class CargaDatosIniciales implements CommandLineRunner {

    // Como queda cada reserva de ejemplo
    private enum Estado { DEVUELTA, CANCELADA, PENDIENTE }

    private final RolServicio rolServicio;
    private final UsuarioServicio usuarioServicio;
    private final PersonaRepositorio personaRepositorio;
    private final OficinaRepositorio oficinaRepositorio;
    private final TipoModeloRepositorio tipoModeloRepositorio;
    private final ModeloRepositorio modeloRepositorio;
    private final VehiculoRepositorio vehiculoRepositorio;
    private final ReservaRepositorio reservaRepositorio;
    private final CalculadoraPrecio calculadoraPrecio;

    @Override
    public void run(String... args) {
        if (personaRepositorio.count() > 0) {
            return;
        }

        // Personas: empleados y clientes
        persona(2, "Sysadmin inicial", "sysadmin@sixt.com.ar", "1100000001", "Av. Corrientes 1000, CABA", "sysadmin123", "SYSADMIN");
        persona(1, "Administrador inicial", "admin@sixt.com.ar", "1100000000", "Av. Corrientes 1000, CABA", "admin123", "ADMIN");
        Persona pepe = persona(30111222, "Pepe Vendedor", "pepe@sixt.com.ar", "1133445566", "Av. Rivadavia 2000, CABA", "vendedor123", "VENDEDOR");
        Persona lucia = persona(31444555, "Lucía Fernández", "lucia@sixt.com.ar", "3514556677", "Bv. San Juan 450, Córdoba", "vendedor123", "VENDEDOR");
        Persona ema = persona(38258469, "Ema Morano", "ema@gmail.com", "1133168094", "Av. Acoyte 300, CABA", "cliente123", "CLIENTE");
        Persona martin = persona(35777888, "Martín Gómez", "martin@gmail.com", "3415667788", "Bv. Oroño 1200, Rosario", "cliente123", "CLIENTE");
        Persona sofia = persona(40123456, "Sofía Ruiz", "sofia@gmail.com", "2614223344", "Av. San Martín 800, Mendoza", "cliente123", "CLIENTE");
        Persona juan = persona(33999000, "Juan Pérez", "juan@gmail.com", "1145678901", "Av. Cabildo 2500, CABA", "cliente123", "CLIENTE");

        // Oficinas en ciudades de Argentina. La primera es donde arranca la flota original
        Oficina buenosAires = oficina("Buenos Aires", "Av. Corrientes 1200, CABA");
        Oficina cordoba = oficina("Córdoba", "Av. Colón 350, Córdoba");
        Oficina rosario = oficina("Rosario", "Bv. Oroño 1500, Rosario");
        Oficina mendoza = oficina("Mendoza", "Av. San Martín 1100, Mendoza");

        // Catalogo: tipos con su precio por dia y modelos
        TipoModelo auto = tipoModelo("AUTO", "10000");
        TipoModelo camioneta = tipoModelo("CAMIONETA", "18000");
        TipoModelo suv = tipoModelo("SUV", "15000");

        Modelo p206 = modelo("206", "Peugeot", auto);
        Modelo hilux = modelo("Hilux", "Toyota", camioneta);
        Modelo cronos = modelo("Cronos", "Fiat", auto);
        Modelo tracker = modelo("Tracker", "Chevrolet", suv);
        Modelo amarok = modelo("Amarok", "Volkswagen", camioneta);

        // Flota: cada auto en la oficina donde termino su ultima reserva devuelta
        Vehiculo peugeot = vehiculo("APG023", "violeta", 2010, p206, buenosAires);
        Vehiculo camionetaHilux = vehiculo("AE123CD", "blanco", 2022, hilux, buenosAires);
        Vehiculo cronosRojo = vehiculo("AF456GH", "rojo", 2023, cronos, cordoba);
        Vehiculo trackerGris = vehiculo("AG789JK", "gris", 2024, tracker, rosario);
        Vehiculo amarokNegra = vehiculo("AD321LM", "negro", 2021, amarok, cordoba);
        Vehiculo cronosBlanco = vehiculo("AH654NP", "blanco", 2024, cronos, cordoba);

        // Reservas ya devueltas (historial para los reportes)
        reserva(sofia, pepe, buenosAires, buenosAires, -150, -148, Estado.DEVUELTA, peugeot, camionetaHilux);
        reserva(martin, lucia, cordoba, rosario, -120, -116, Estado.DEVUELTA, cronosRojo);
        reserva(martin, null, rosario, rosario, -90, -85, Estado.DEVUELTA, trackerGris);
        reserva(ema, lucia, rosario, cordoba, -60, -57, Estado.DEVUELTA, cronosRojo);
        reserva(ema, pepe, buenosAires, buenosAires, -45, -43, Estado.DEVUELTA, peugeot);
        reserva(juan, pepe, mendoza, cordoba, -30, -26, Estado.DEVUELTA, amarokNegra);
        reserva(sofia, lucia, cordoba, cordoba, -10, -8, Estado.DEVUELTA, cronosBlanco);
        reserva(juan, null, buenosAires, buenosAires, -5, -3, Estado.DEVUELTA, camionetaHilux);

        // Una cancelada: no se factura, aparece en el contador de canceladas
        reserva(martin, lucia, rosario, rosario, -20, -18, Estado.CANCELADA, trackerGris);

        // Proximas reservas, todavia sin retirar
        reserva(ema, lucia, cordoba, mendoza, 10, 13, Estado.PENDIENTE, cronosRojo);
        reserva(sofia, null, rosario, buenosAires, 15, 18, Estado.PENDIENTE, trackerGris);
    }

    // Primero la cuenta (app seguridad) y despues la persona, que comparte su id
    private Persona persona(int dni, String nombre, String email, String telefono, String direccion,
                            String contrasenia, String rol) {
        Persona p = new Persona();
        p.setUsuario(usuarioServicio.crear(email, contrasenia, rolServicio.buscarPorNombre(rol)));
        p.setDni(dni);
        p.setNombre(nombre);
        p.setTelefono(telefono);
        p.setDireccion(direccion);
        return personaRepositorio.save(p);
    }

    private Oficina oficina(String zona, String direccion) {
        Oficina o = new Oficina();
        o.setZona(zona);
        o.setDireccion(direccion);
        return oficinaRepositorio.save(o);
    }

    private TipoModelo tipoModelo(String nombre, String precio) {
        TipoModelo t = new TipoModelo();
        t.setNombre(nombre);
        t.setPrecio(new BigDecimal(precio));
        return tipoModeloRepositorio.save(t);
    }

    private Modelo modelo(String nombre, String marca, TipoModelo tipo) {
        Modelo m = new Modelo();
        m.setNombre(nombre);
        m.setMarca(marca);
        m.setTipo(tipo);
        return modeloRepositorio.save(m);
    }

    private Vehiculo vehiculo(String patente, String color, int anio, Modelo modelo, Oficina oficina) {
        Vehiculo v = new Vehiculo();
        v.setPatente(patente);
        v.setColor(color);
        v.setAnio(anio);
        v.setModelo(modelo);
        v.setOficina(oficina);
        return vehiculoRepositorio.save(v);
    }

    // Arma la reserva igual que ReservaServicio (precio con la CalculadoraPrecio, copia de los datos
    // de cada vehiculo), pero sin sus validaciones: el historial puede tener fechas pasadas.
    // Los dias son relativos a hoy: -10 es hace diez dias, 15 es dentro de quince.
    // vendedor null = reserva online, hecha por el propio cliente.
    private void reserva(Persona cliente, Persona vendedor, Oficina origen, Oficina destino,
                         int desdeDias, int hastaDias, Estado estado, Vehiculo... vehiculos) {
        LocalDate hoy = LocalDate.now();
        LocalDate desde = hoy.plusDays(desdeDias);
        LocalDate hasta = hoy.plusDays(hastaDias);

        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setVendedor(vendedor);
        reserva.setOficinaOrigen(origen);
        reserva.setOficinaDestino(destino);
        reserva.setFechaDesde(desde);
        reserva.setFechaHasta(hasta);

        BigDecimal total = BigDecimal.ZERO;
        for (Vehiculo vehiculo : vehiculos) {
            DetalleReserva detalle = new DetalleReserva();
            detalle.setVehiculo(vehiculo);
            detalle.setPatente(vehiculo.getPatente());
            detalle.setColor(vehiculo.getColor());
            detalle.setAnio(vehiculo.getAnio());
            detalle.setLitrosNafta(30);
            detalle.setPrecioAlquiler(calculadoraPrecio.calcular(vehiculo, desde, hasta));
            total = total.add(detalle.getPrecioAlquiler());
            reserva.agregarDetalle(detalle);
        }
        reserva.setPrecioTotal(total);
        reserva.setDevuelto(estado == Estado.DEVUELTA);
        if (estado == Estado.CANCELADA) {
            reserva.setFechaCancelacion(Instant.now());
        }
        reservaRepositorio.save(reserva);
    }
}
