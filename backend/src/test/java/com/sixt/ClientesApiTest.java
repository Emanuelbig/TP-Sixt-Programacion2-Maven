package com.sixt;

import com.sixt.modelo.Permiso;
import com.sixt.repositorio.PersonaRepositorio;
import com.sixt.repositorio.RolRepositorio;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.ObjectMapper;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:clientes-api;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa", "spring.datasource.password=", "spring.jpa.show-sql=false"})
class ClientesApiTest {
    @LocalServerPort int puerto;
    @Autowired ObjectMapper json;
    @Autowired PersonaRepositorio personas;
    @Autowired RolRepositorio roles;
    private static final AtomicInteger secuencia = new AtomicInteger(50000000);

    private HttpClient clienteHttp() {
        return HttpClient.newBuilder().cookieHandler(new CookieManager()).build();
    }
    private HttpResponse<String> enviar(HttpClient cliente, String metodo, String ruta, Object datos) throws Exception {
        var cuerpo = datos == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(json.writeValueAsString(datos));
        return cliente.send(HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + ruta))
                .header("Content-Type", "application/json").method(metodo, cuerpo).build(),
                HttpResponse.BodyHandlers.ofString());
    }
    private HttpClient login(String email, String clave) throws Exception {
        var cliente = clienteHttp();
        assertThat(enviar(cliente, "POST", "/api/login", Map.of("email", email, "contrasenia", clave)).statusCode()).isEqualTo(200);
        return cliente;
    }
    private Map<String, Object> datos() {
        int numero = secuencia.incrementAndGet();
        return new java.util.LinkedHashMap<>(Map.of("dni", numero, "nombre", "Cliente prueba",
                "direccion", "Av. Corrientes 123", "email", "cliente" + numero + "@example.com",
                "telefono", "1122334455", "contrasenia", "prueba123"));
    }
    private long crear(HttpClient cliente, Map<String, Object> datos) throws Exception {
        var respuesta = enviar(cliente, "POST", "/api/clientes", datos);
        assertThat(respuesta.statusCode()).as(respuesta.body()).isEqualTo(201);
        return json.readTree(respuesta.body()).get("id").asLong();
    }

    @Test
    void sinSesionNoSePuedeUsarLaApiNiSuplantarAlUsuario() throws Exception {
        var respuesta = clienteHttp().send(HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + "/api/clientes"))
                .header("usuarioId", "1").header("X-User-Id", "1").GET().build(), HttpResponse.BodyHandlers.ofString());
        assertThat(respuesta.statusCode()).isEqualTo(401);
        assertThat(enviar(clienteHttp(), "GET", "/api/personas", null).statusCode()).isEqualTo(401);
        for (String ruta : new String[] {"/%61pi/personas", "/api;parametro=1/personas"}) {
            assertThat(enviar(clienteHttp(), "GET", ruta, null).statusCode()).isIn(400, 401);
        }
    }

    @Test
    void vendedorAdministraClientesSinPoderCambiarSuRol() throws Exception {
        var vendedor = login("pepe@sixt.com.ar", "vendedor123");
        var datos = datos();
        datos.put("rolId", roles.findByNombreIgnoreCase("ADMIN").orElseThrow().getId());
        long id = crear(vendedor, datos);
        var persona = personas.findById(id).orElseThrow();
        // El rol proviene del servidor, no del rolId agregado al JSON.
        assertThat(json.readTree(enviar(vendedor, "GET", "/api/clientes", null).body()).toString()).contains("CLIENTE");
        datos.put("direccion", "Av. Rivadavia 456");
        assertThat(enviar(vendedor, "PUT", "/api/clientes/" + id, datos).statusCode()).isEqualTo(200);
        assertThat(personas.findById(id).orElseThrow().getDireccion()).isEqualTo("Av. Rivadavia 456");
        var lista = json.readTree(enviar(vendedor, "GET", "/api/clientes", null).body());
        for (var fila : lista) {
            assertThat(fila.get("rol").get("nombre").asText()).isEqualTo("CLIENTE");
            assertThat(fila.get("rol").get("permisos").size()).isEqualTo(1);
        }
        assertThat(enviar(vendedor, "DELETE", "/api/clientes/" + id, null).statusCode()).isEqualTo(204);
        assertThat(enviar(clienteHttp(), "POST", "/api/login", Map.of("email", persona.getEmail(), "contrasenia", "prueba123")).statusCode()).isEqualTo(401);
    }

    @Test
    void vendedorNoAdministraPersonasRolesNiCuentasPrivilegiadas() throws Exception {
        var vendedor = login("pepe@sixt.com.ar", "vendedor123");
        for (String ruta : new String[] {"/api/personas", "/api/roles", "/api/permisos"}) {
            assertThat(enviar(vendedor, "GET", ruta, null).statusCode()).isEqualTo(403);
            assertThat(enviar(vendedor, "POST", ruta, datos()).statusCode()).isEqualTo(403);
        }
        long admin = personas.findByEmailIgnoreCase("admin@sixt.com.ar").orElseThrow().getId();
        long pepe = personas.findByEmailIgnoreCase("pepe@sixt.com.ar").orElseThrow().getId();
        for (long id : new long[] {admin, pepe}) {
            assertThat(enviar(vendedor, "PUT", "/api/clientes/" + id, datos()).statusCode()).isEqualTo(403);
            assertThat(enviar(vendedor, "DELETE", "/api/clientes/" + id, null).statusCode()).isEqualTo(403);
            assertThat(enviar(vendedor, "PUT", "/api/personas/" + id, datos()).statusCode()).isEqualTo(403);
            assertThat(enviar(vendedor, "DELETE", "/api/personas/" + id, null).statusCode()).isEqualTo(403);
        }
    }

    @Test
    void clienteNoGestionaClientesYNoConsultaReservasDeOtro() throws Exception {
        var cliente = login("ema@gmail.com", "cliente123");
        for (String metodo : new String[] {"GET", "POST", "PUT", "DELETE"}) {
            String ruta = metodo.equals("GET") || metodo.equals("POST") ? "/api/clientes" : "/api/clientes/1";
            assertThat(enviar(cliente, metodo, ruta, metodo.equals("POST") || metodo.equals("PUT") ? datos() : null).statusCode()).isEqualTo(403);
        }
        assertThat(enviar(cliente, "GET", "/api/personas", null).statusCode()).isEqualTo(403);
        assertThat(enviar(cliente, "GET", "/api/reservas", null).statusCode()).isEqualTo(200);
        assertThat(enviar(cliente, "GET", "/api/reservas?clienteId=999999", null).statusCode()).isEqualTo(403);
    }

    @Test
    void administradorConservaGestionGeneralYDeClientes() throws Exception {
        var admin = login("admin@sixt.com.ar", "admin123");
        for (String ruta : new String[] {"/api/personas", "/api/roles", "/api/clientes", "/api/vehiculos", "/api/oficinas"}) {
            assertThat(enviar(admin, "GET", ruta, null).statusCode()).isEqualTo(200);
        }
        long id = crear(admin, datos());
        assertThat(enviar(admin, "DELETE", "/api/clientes/" + id, null).statusCode()).isEqualTo(204);
    }

    @Test
    void validacionesYBajaConReservasPendientesSiguenVigentes() throws Exception {
        var vendedor = login("pepe@sixt.com.ar", "vendedor123");
        var datos = datos();
        long id = crear(vendedor, datos);
        assertThat(enviar(vendedor, "POST", "/api/clientes", datos).statusCode()).isEqualTo(409);
        datos.put("direccion", " ");
        assertThat(enviar(vendedor, "PUT", "/api/clientes/" + id, datos).statusCode()).isEqualTo(400);
        long vendedorId = personas.findByEmailIgnoreCase("pepe@sixt.com.ar").orElseThrow().getId();
        var vehiculos = json.readTree(enviar(vendedor, "GET", "/api/vehiculos", null).body());
        var oficinas = json.readTree(enviar(vendedor, "GET", "/api/oficinas", null).body());
        long oficinaId = oficinas.get(0).get("id").asLong();
        var reserva = Map.of("clienteId", id, "vendedorId", vendedorId, "oficinaOrigenId", oficinaId,
                "oficinaDestinoId", oficinaId, "fechaDesde", LocalDate.now().plusDays(30).toString(),
                "fechaHasta", LocalDate.now().plusDays(31).toString(), "detalles",
                java.util.List.of(Map.of("vehiculoId", vehiculos.get(0).get("id").asLong(), "litrosNafta", 20)));
        var respuesta = enviar(vendedor, "POST", "/api/reservas", reserva);
        assertThat(respuesta.statusCode()).as(respuesta.body()).isEqualTo(201);
        long reservaId = json.readTree(respuesta.body()).get("id").asLong();
        assertThat(enviar(vendedor, "DELETE", "/api/clientes/" + id, null).statusCode()).isEqualTo(409);
        assertThat(enviar(vendedor, "POST", "/api/reservas/" + reservaId + "/devolver", Map.of()).statusCode()).isEqualTo(200);
        assertThat(enviar(vendedor, "DELETE", "/api/clientes/" + id, null).statusCode()).isEqualTo(204);
    }

    @Test
    void logoutInvalidaLaSesionYLasSesionesSonIndependientes() throws Exception {
        var vendedor = login("pepe@sixt.com.ar", "vendedor123");
        var cliente = login("ema@gmail.com", "cliente123");
        assertThat(enviar(cliente, "GET", "/api/clientes", null).statusCode()).isEqualTo(403);
        assertThat(enviar(vendedor, "GET", "/api/clientes", null).statusCode()).isEqualTo(200);
        assertThat(enviar(vendedor, "POST", "/api/logout", Map.of()).statusCode()).isEqualTo(204);
        assertThat(enviar(vendedor, "GET", "/api/clientes", null).statusCode()).isEqualTo(401);
        assertThat(enviar(cliente, "GET", "/api/sesion", null).statusCode()).isEqualTo(200);
    }

    @Test
    void noSeAceptaCambiarLaContraseniaDeOtraPersona() throws Exception {
        var vendedor = login("pepe@sixt.com.ar", "vendedor123");
        long adminId = personas.findByEmailIgnoreCase("admin@sixt.com.ar").orElseThrow().getId();
        assertThat(enviar(vendedor, "PATCH", "/api/personas/" + adminId + "/contrasenia",
                Map.of("contraseniaActual", "admin123", "contraseniaNueva", "cambio123")).statusCode()).isEqualTo(403);
        long pepeId = personas.findByEmailIgnoreCase("pepe@sixt.com.ar").orElseThrow().getId();
        assertThat(enviar(vendedor, "PATCH", "/api/personas/" + pepeId + "/contrasenia",
                Map.of("contraseniaActual", "incorrecta", "contraseniaNueva", "cambio123")).statusCode()).isEqualTo(400);
    }
    @Test
    void revocarPermisoSeAplicaALaSesionExistente() throws Exception {
        var vendedor = login("pepe@sixt.com.ar", "vendedor123");
        var rol = roles.findByNombreIgnoreCase("VENDEDOR").orElseThrow();
        try {
            rol.getPermisos().remove(Permiso.ADMINISTRAR_CLIENTES);
            rol = roles.saveAndFlush(rol);
            assertThat(enviar(vendedor, "GET", "/api/clientes", null).statusCode()).isEqualTo(403);
        } finally {
            rol.getPermisos().add(Permiso.ADMINISTRAR_CLIENTES);
            roles.saveAndFlush(rol);
        }
        assertThat(enviar(vendedor, "GET", "/api/clientes", null).statusCode()).isEqualTo(200);
    }

}
