package com.sixt.seguridad.configuracion;

import static com.sixt.seguridad.modelo.Accion.ALQUILAR;
import static com.sixt.seguridad.modelo.Accion.CREAR;
import static com.sixt.seguridad.modelo.Accion.EDITAR;
import static com.sixt.seguridad.modelo.Accion.ELIMINAR;
import static com.sixt.seguridad.modelo.Accion.VER;
import static com.sixt.seguridad.modelo.Accion.VER_PROPIAS;
import static com.sixt.seguridad.modelo.Recurso.CLIENTE;
import static com.sixt.seguridad.modelo.Recurso.OFICINA;
import static com.sixt.seguridad.modelo.Recurso.REPORTE;
import static com.sixt.seguridad.modelo.Recurso.RESERVA;
import static com.sixt.seguridad.modelo.Recurso.ROL;
import static com.sixt.seguridad.modelo.Recurso.USUARIO;
import static com.sixt.seguridad.modelo.Recurso.VEHICULO;

import com.sixt.seguridad.modelo.Accion;
import com.sixt.seguridad.modelo.Permiso;
import com.sixt.seguridad.modelo.Recurso;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

// Que authority pide cada URL y metodo HTTP. Gana la primera regla que coincide,
// por eso las particulares van antes que las generales.
// Agregar un recurso nuevo es una linea en configurar(): abierto a extension (OCP).
class ReglasDeAcceso implements Customizer<
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> {

    @Override
    public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry reglas) {
        reglas.requestMatchers(HttpMethod.POST, "/api/login").permitAll();
        reglas.requestMatchers("/error").permitAll();

        // Lecturas que necesita el buscador de reservas: lo usan vendedores y clientes
        reglas.requestMatchers(HttpMethod.GET, "/api/vehiculos/disponibles")
                .hasAnyAuthority(permiso(VEHICULO, VER), permiso(RESERVA, ALQUILAR));
        reglas.requestMatchers(HttpMethod.GET, "/api/oficinas")
                .hasAnyAuthority(permiso(OFICINA, VER), permiso(RESERVA, ALQUILAR));
        reglas.requestMatchers("/api/permisos").hasAuthority(permiso(ROL, VER));
        // El formulario de personas necesita la lista de roles
        reglas.requestMatchers(HttpMethod.GET, "/api/roles", "/api/roles/**")
                .hasAnyAuthority(permiso(ROL, VER), permiso(USUARIO, VER));
        // Un cliente ve solo las suyas: lo controla un @PreAuthorize en ReservaControlador
        reglas.requestMatchers(HttpMethod.GET, "/api/reservas", "/api/reservas/**")
                .hasAuthority(permiso(RESERVA, VER_PROPIAS));
        reglas.requestMatchers(HttpMethod.POST, "/api/reservas/*/devolver").hasAuthority(permiso(RESERVA, EDITAR));
        // Cancelar: con RESERVA:EDITAR cualquiera, el cliente solo las suyas (lo controla ReservaControlador)
        reglas.requestMatchers(HttpMethod.POST, "/api/reservas/*/cancelar")
                .hasAnyAuthority(permiso(RESERVA, EDITAR), permiso(RESERVA, ALQUILAR));
        // Reserva de un vendedor (CREAR) o reserva online del propio cliente (ALQUILAR):
        // ReservaControlador decide cual de las dos es
        reglas.requestMatchers(HttpMethod.POST, "/api/reservas")
                .hasAnyAuthority(permiso(RESERVA, CREAR), permiso(RESERVA, ALQUILAR));

        porRecurso(reglas, "/api/oficinas", OFICINA);
        porRecurso(reglas, "/api/vehiculos", VEHICULO);
        // Modelos y tipos (con sus precios) son el catalogo de la flota
        porRecurso(reglas, "/api/modelos", VEHICULO);
        porRecurso(reglas, "/api/tipos", VEHICULO);
        // Clientes y empleados comparten pantalla: alcanza con la accion sobre uno de los dos.
        // PersonaServicio exige despues CLIENTE o USUARIO segun el rol de la persona que se toca
        porRecurso(reglas, "/api/personas", CLIENTE, USUARIO);
        porRecurso(reglas, "/api/reservas", RESERVA);
        porRecurso(reglas, "/api/roles", ROL);
        porRecurso(reglas, "/api/reportes", REPORTE);

        // /api/sesion, /api/logout, /api/cuenta/contrasenia, /api/resumen: alcanza con estar identificado
        reglas.anyRequest().authenticated();
    }

    // Mismo patron para cada recurso: GET -> VER, POST -> CREAR, PUT y PATCH -> EDITAR,
    // DELETE -> ELIMINAR. Con varios recursos alcanza con tener la accion sobre alguno.
    // Si ninguno admite la accion (por ejemplo, eliminar un reporte) se deniega siempre.
    private static void porRecurso(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry reglas,
            String ruta, Recurso... recursos) {
        String[] rutas = {ruta, ruta + "/**"};
        exigir(reglas, HttpMethod.GET, rutas, VER, recursos);
        exigir(reglas, HttpMethod.POST, rutas, CREAR, recursos);
        exigir(reglas, HttpMethod.PUT, rutas, EDITAR, recursos);
        exigir(reglas, HttpMethod.PATCH, rutas, EDITAR, recursos);
        exigir(reglas, HttpMethod.DELETE, rutas, ELIMINAR, recursos);
    }

    private static void exigir(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry reglas,
            HttpMethod metodo, String[] rutas, Accion accion, Recurso[] recursos) {
        List<String> permisos = new ArrayList<>();
        for (Recurso recurso : recursos) {
            if (recurso.admite(accion)) {
                permisos.add(permiso(recurso, accion));
            }
        }
        if (permisos.isEmpty()) {
            reglas.requestMatchers(metodo, rutas).denyAll();
        } else {
            reglas.requestMatchers(metodo, rutas).hasAnyAuthority(permisos.toArray(new String[0]));
        }
    }

    private static String permiso(Recurso recurso, Accion accion) {
        return Permiso.de(recurso, accion).toString();
    }
}
