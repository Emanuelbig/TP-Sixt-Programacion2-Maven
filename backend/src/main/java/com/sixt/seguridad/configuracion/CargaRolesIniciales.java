package com.sixt.seguridad.configuracion;

import static com.sixt.seguridad.modelo.Accion.ADMINISTRAR;
import static com.sixt.seguridad.modelo.Accion.ALQUILAR;
import static com.sixt.seguridad.modelo.Accion.VENDER;
import static com.sixt.seguridad.modelo.Accion.VER;
import static com.sixt.seguridad.modelo.Accion.VER_PROPIAS;
import static com.sixt.seguridad.modelo.Recurso.CLIENTE;
import static com.sixt.seguridad.modelo.Recurso.OFICINA;
import static com.sixt.seguridad.modelo.Recurso.REPORTE;
import static com.sixt.seguridad.modelo.Recurso.RESERVA;
import static com.sixt.seguridad.modelo.Recurso.ROL;
import static com.sixt.seguridad.modelo.Recurso.USUARIO;
import static com.sixt.seguridad.modelo.Recurso.VEHICULO;

import com.sixt.seguridad.modelo.Permiso;
import com.sixt.seguridad.modelo.Recurso;
import com.sixt.seguridad.modelo.Rol;
import com.sixt.seguridad.repositorio.RolRepositorio;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

// Con la base vacia crea los cuatro roles iniciales; despues el sysadmin los puede cambiar.
// Corre antes que la carga de datos de la plataforma, que asigna estos roles a las personas.
@Component
@Order(1)
@RequiredArgsConstructor
public class CargaRolesIniciales implements CommandLineRunner {

    private final RolRepositorio rolRepositorio;

    @Override
    public void run(String... args) {
        if (rolRepositorio.count() > 0) {
            return;
        }
        // La accion mas amplia de cada recurso: ADMINISTRAR, o VER si es de solo lectura (REPORTE)
        Set<Permiso> todoAdministrado = new HashSet<>();
        for (Recurso recurso : Recurso.values()) {
            if (recurso.admite(ADMINISTRAR)) {
                todoAdministrado.add(Permiso.de(recurso, ADMINISTRAR));
            } else {
                todoAdministrado.add(Permiso.de(recurso, VER));
            }
        }
        rolRepositorio.save(new Rol("SYSADMIN", todoAdministrado));
        rolRepositorio.save(new Rol("ADMIN", Set.of(
                Permiso.de(VEHICULO, ADMINISTRAR), Permiso.de(OFICINA, ADMINISTRAR),
                Permiso.de(CLIENTE, ADMINISTRAR), Permiso.de(USUARIO, ADMINISTRAR),
                Permiso.de(RESERVA, ADMINISTRAR), Permiso.de(RESERVA, VENDER), Permiso.de(ROL, VER),
                Permiso.de(REPORTE, VER))));
        rolRepositorio.save(new Rol("VENDEDOR", Set.of(
                Permiso.de(RESERVA, ADMINISTRAR), Permiso.de(RESERVA, VENDER), Permiso.de(CLIENTE, ADMINISTRAR),
                Permiso.de(VEHICULO, VER), Permiso.de(OFICINA, VER))));
        rolRepositorio.save(new Rol("CLIENTE", Set.of(
                Permiso.de(RESERVA, VER_PROPIAS), Permiso.de(RESERVA, ALQUILAR))));
    }
}
