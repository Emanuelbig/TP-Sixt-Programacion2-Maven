package ar.edu.utn.inspt.sixt.configuracion;

import ar.edu.utn.inspt.sixt.modelo.Modelo;
import ar.edu.utn.inspt.sixt.modelo.Oficina;
import ar.edu.utn.inspt.sixt.modelo.Permiso;
import ar.edu.utn.inspt.sixt.modelo.Persona;
import ar.edu.utn.inspt.sixt.modelo.Rol;
import ar.edu.utn.inspt.sixt.modelo.TipoModelo;
import ar.edu.utn.inspt.sixt.modelo.Vehiculo;
import ar.edu.utn.inspt.sixt.repositorio.ModeloRepositorio;
import ar.edu.utn.inspt.sixt.repositorio.OficinaRepositorio;
import ar.edu.utn.inspt.sixt.repositorio.PersonaRepositorio;
import ar.edu.utn.inspt.sixt.repositorio.RolRepositorio;
import ar.edu.utn.inspt.sixt.repositorio.TipoModeloRepositorio;
import ar.edu.utn.inspt.sixt.repositorio.VehiculoRepositorio;
import java.math.BigDecimal;
import java.util.EnumSet;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// Carga datos de ejemplo la primera vez (si la base esta vacia),
// tomados de los .txt de la version de consola.
// Usuarios de prueba: admin@sixt.com.ar / admin123, pepe@sixt.com.ar / vendedor123,
// ema@gmail.com / cliente123
@Component
@RequiredArgsConstructor
public class CargaDatosIniciales implements CommandLineRunner {

    private final RolRepositorio rolRepositorio;
    private final PersonaRepositorio personaRepositorio;
    private final OficinaRepositorio oficinaRepositorio;
    private final TipoModeloRepositorio tipoModeloRepositorio;
    private final ModeloRepositorio modeloRepositorio;
    private final VehiculoRepositorio vehiculoRepositorio;
    private final PasswordEncoder codificador;

    @Override
    public void run(String... args) {
        if (rolRepositorio.count() > 0) {
            return;
        }

        Rol admin = rol("ADMIN", Permiso.ADMINISTRAR, Permiso.VENDER);
        Rol vendedor = rol("VENDEDOR", Permiso.VENDER);
        Rol cliente = rol("CLIENTE", Permiso.ALQUILAR);

        persona(1, "Administrador inicial", "admin@sixt.com.ar", "1100000000", "admin123", admin);
        persona(30111222, "Pepe Vendedor", "pepe@sixt.com.ar", "1133445566", "vendedor123", vendedor);
        persona(38258469, "Ema Morano", "ema@gmail.com", "1133168094", "cliente123", cliente);

        Oficina ofi1 = oficina("Ofi 1", "Corrientes y Callao");
        oficina("Ofi 2", "Acoyte 721");
        oficina("Acoyte y Rivadavia", "Acoyte 19");

        TipoModelo auto = tipoModelo("AUTO", "10000");
        TipoModelo camioneta = tipoModelo("CAMIONETA", "18000");

        Modelo p206 = modelo("206", "Peugeot", auto);
        Modelo hilux = modelo("Hilux", "Toyota", camioneta);

        vehiculo("APG023", "violeta", 2010, p206, ofi1);
        vehiculo("AE123CD", "blanco", 2022, hilux, ofi1);
    }

    private Rol rol(String nombre, Permiso permiso, Permiso... otros) {
        Rol r = new Rol();
        r.setNombre(nombre);
        r.setPermisos(EnumSet.of(permiso, otros));
        return rolRepositorio.save(r);
    }

    private void persona(int dni, String nombre, String email, String telefono, String contrasenia, Rol rol) {
        Persona p = new Persona();
        p.setDni(dni);
        p.setNombre(nombre);
        p.setEmail(email);
        p.setTelefono(telefono);
        p.setContrasenia(codificador.encode(contrasenia));
        p.setRol(rol);
        personaRepositorio.save(p);
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

    private void vehiculo(String patente, String color, int anio, Modelo modelo, Oficina oficina) {
        Vehiculo v = new Vehiculo();
        v.setPatente(patente);
        v.setColor(color);
        v.setAnio(anio);
        v.setModelo(modelo);
        v.setOficina(oficina);
        vehiculoRepositorio.save(v);
    }
}
