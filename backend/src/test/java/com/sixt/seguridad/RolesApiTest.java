package com.sixt.seguridad;

import static org.assertj.core.api.Assertions.assertThat;

import com.sixt.seguridad.repositorio.RolRepositorio;
import com.sixt.seguridad.repositorio.UsuarioRepositorio;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.ObjectMapper;

// Reglas contra la escalada de privilegios al administrar roles y personas
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:roles-api;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa", "spring.datasource.password=", "spring.jpa.show-sql=false"})
class RolesApiTest {

    @LocalServerPort int puerto;
    @Autowired ObjectMapper json;
    @Autowired RolRepositorio roles;
    @Autowired UsuarioRepositorio usuarios;
    private static final AtomicInteger secuencia = new AtomicInteger(60000000);
    private final HttpClient http = HttpClient.newHttpClient();

    private HttpResponse<String> enviar(String token, String metodo, String ruta, Object datos) throws Exception {
        var cuerpo = datos == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(json.writeValueAsString(datos));
        var pedido = HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + ruta))
                .header("Content-Type", "application/json").method(metodo, cuerpo);
        if (token != null) pedido.header("Authorization", "Bearer " + token);
        return http.send(pedido.build(), HttpResponse.BodyHandlers.ofString());
    }

    private String login(String email, String clave) throws Exception {
        var respuesta = enviar(null, "POST", "/api/login", Map.of("email", email, "contrasenia", clave));
        assertThat(respuesta.statusCode()).as(respuesta.body()).isEqualTo(200);
        return json.readTree(respuesta.body()).get("token").asText();
    }

    private long crearRol(String token, String nombre, String... permisos) throws Exception {
        var respuesta = enviar(token, "POST", "/api/roles", Map.of("nombre", nombre, "permisos", List.of(permisos)));
        assertThat(respuesta.statusCode()).as(respuesta.body()).isEqualTo(201);
        return json.readTree(respuesta.body()).get("id").asLong();
    }

    // Crea una persona con ese rol y devuelve su email (contraseña: prueba123)
    private String crearPersona(String token, long rolId) throws Exception {
        int numero = secuencia.incrementAndGet();
        String email = "persona" + numero + "@sixt.com.ar";
        var datos = new LinkedHashMap<String, Object>(Map.of("dni", numero, "nombre", "Persona " + numero,
                "direccion", "Av. Corrientes 123", "email", email, "telefono", "1122334455",
                "rolId", rolId, "contrasenia", "prueba123"));
        var respuesta = enviar(token, "POST", "/api/personas", datos);
        assertThat(respuesta.statusCode()).as(respuesta.body()).isEqualTo(201);
        return email;
    }

    private long rol(String nombre) {
        return roles.findByNombreIgnoreCase(nombre).orElseThrow().getId();
    }

    @Test
    void elSysadminCreaUnRolNuevoQueSeAplicaEnVivo() throws Exception {
        var sysadmin = login("sysadmin@sixt.com.ar", "sysadmin123");
        long auditor = crearRol(sysadmin, "auditor", "VEHICULO:VER", "OFICINA:VER", "RESERVA:VER");
        var token = login(crearPersona(sysadmin, auditor), "prueba123");

        assertThat(enviar(token, "GET", "/api/vehiculos", null).statusCode()).isEqualTo(200);
        assertThat(enviar(token, "GET", "/api/reservas", null).statusCode()).isEqualTo(200);
        assertThat(enviar(token, "POST", "/api/oficinas", Map.of("zona", "x", "direccion", "y")).statusCode()).isEqualTo(403);
        assertThat(enviar(token, "GET", "/api/personas", null).statusCode()).isEqualTo(403);
    }

    @Test
    void elCatalogoYLosPermisosInvalidos() throws Exception {
        var sysadmin = login("sysadmin@sixt.com.ar", "sysadmin123");
        var catalogo = json.readTree(enviar(sysadmin, "GET", "/api/permisos", null).body());
        assertThat(catalogo.size()).isEqualTo(7);
        assertThat(catalogo.toString()).contains("VEHICULO:ADMINISTRAR", "RESERVA:VER_PROPIAS");

        assertThat(enviar(sysadmin, "POST", "/api/roles", Map.of("nombre", "MAL", "permisos", List.of("AVION:VER")))
                .statusCode()).isEqualTo(400);
        assertThat(enviar(sysadmin, "POST", "/api/roles", Map.of("nombre", "MAL", "permisos", List.of("REPORTE:CREAR")))
                .statusCode()).isEqualTo(400);
        assertThat(enviar(sysadmin, "POST", "/api/roles", Map.of("nombre", "cliente", "permisos", List.of("OFICINA:VER")))
                .statusCode()).isEqualTo(409);
    }

    @Test
    void nadieOtorgaPermisosQueNoTiene() throws Exception {
        var sysadmin = login("sysadmin@sixt.com.ar", "sysadmin123");
        // Un encargado de roles que no administra vehiculos
        long encargado = crearRol(sysadmin, "ENCARGADO_ROLES", "ROL:ADMINISTRAR", "USUARIO:ADMINISTRAR", "OFICINA:VER");
        var token = login(crearPersona(sysadmin, encargado), "prueba123");

        var intento = enviar(token, "POST", "/api/roles", Map.of("nombre", "FLOTA", "permisos", List.of("VEHICULO:EDITAR")));
        assertThat(intento.statusCode()).isEqualTo(403);
        assertThat(intento.body()).contains("No podes otorgar");
        crearRol(token, "SOLO_OFICINAS", "OFICINA:VER");

        // Tampoco puede editar ni borrar un rol con mas permisos que el, ni asignarlo
        var vacio = Map.of("nombre", "SYSADMIN", "permisos", List.of("OFICINA:VER"));
        assertThat(enviar(token, "PUT", "/api/roles/" + rol("SYSADMIN"), vacio).statusCode()).isEqualTo(403);
        assertThat(enviar(token, "DELETE", "/api/roles/" + rol("ADMIN"), null).statusCode()).isEqualTo(403);
        var datos = new LinkedHashMap<String, Object>(Map.of("dni", secuencia.incrementAndGet(), "nombre", "Otro",
                "direccion", "Av. 1", "email", "otro" + secuencia.get() + "@sixt.com.ar", "telefono", "1122334455",
                "rolId", rol("SYSADMIN"), "contrasenia", "prueba123"));
        assertThat(enviar(token, "POST", "/api/personas", datos).statusCode()).isEqualTo(403);
    }

    @Test
    void nadieModificaSuPropioRolNiSeDaDeBaja() throws Exception {
        var sysadmin = login("sysadmin@sixt.com.ar", "sysadmin123");
        long propio = usuarios.findByEmailIgnoreCase("sysadmin@sixt.com.ar").orElseThrow().getId();
        var cambio = Map.of("nombre", "SYSADMIN", "permisos", List.of("ROL:ADMINISTRAR"));

        assertThat(enviar(sysadmin, "PUT", "/api/roles/" + rol("SYSADMIN"), cambio).statusCode()).isEqualTo(403);
        assertThat(enviar(sysadmin, "DELETE", "/api/roles/" + rol("SYSADMIN"), null).statusCode()).isEqualTo(403);
        assertThat(enviar(sysadmin, "DELETE", "/api/personas/" + propio, null).statusCode()).isEqualTo(403);
        var datos = Map.of("dni", 2, "nombre", "Sysadmin inicial", "direccion", "Av. Corrientes 1000",
                "email", "sysadmin@sixt.com.ar", "telefono", "1100000001", "rolId", rol("ADMIN"));
        assertThat(enviar(sysadmin, "PUT", "/api/personas/" + propio, datos).statusCode()).isEqualTo(403);
    }

    @Test
    void noSeBorraUnRolConUsuariosYSiUnoSinUsuarios() throws Exception {
        var sysadmin = login("sysadmin@sixt.com.ar", "sysadmin123");
        assertThat(enviar(sysadmin, "DELETE", "/api/roles/" + rol("VENDEDOR"), null).statusCode()).isEqualTo(409);
        long libre = crearRol(sysadmin, "TEMPORAL", "OFICINA:VER");
        assertThat(enviar(sysadmin, "DELETE", "/api/roles/" + libre, null).statusCode()).isEqualTo(204);
    }

    @Test
    void unUsuarioDadoDeBajaPierdeElAccesoAunqueSuTokenNoHayaVencido() throws Exception {
        var sysadmin = login("sysadmin@sixt.com.ar", "sysadmin123");
        String email = crearPersona(sysadmin, rol("VENDEDOR"));
        var token = login(email, "prueba123");
        assertThat(enviar(token, "GET", "/api/sesion", null).statusCode()).isEqualTo(200);

        long id = usuarios.findByEmailIgnoreCase(email).orElseThrow().getId();
        assertThat(enviar(sysadmin, "DELETE", "/api/personas/" + id, null).statusCode()).isEqualTo(204);
        assertThat(enviar(token, "GET", "/api/sesion", null).statusCode()).isEqualTo(401);
    }
}
