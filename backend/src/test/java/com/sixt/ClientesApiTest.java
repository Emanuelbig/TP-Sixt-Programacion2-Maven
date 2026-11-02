package com.sixt;

import com.sixt.repositorio.PersonaRepositorio;
import com.sixt.seguridad.repositorio.RolRepositorio;
import com.sixt.seguridad.repositorio.UsuarioRepositorio;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.ObjectMapper;
import static org.assertj.core.api.Assertions.assertThat;

// Pruebas de punta a punta por HTTP: login, token en el header Authorization y respuestas 401/403
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:clientes-api;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa", "spring.datasource.password=", "spring.jpa.show-sql=false"})
class ClientesApiTest {
    @LocalServerPort int puerto;
    @Autowired ObjectMapper json;
    @Autowired PersonaRepositorio personas;
    @Autowired UsuarioRepositorio usuarios;
    @Autowired RolRepositorio roles;
    private static final AtomicInteger secuencia = new AtomicInteger(50000000);
    private final HttpClient http = HttpClient.newHttpClient();

    // token == null: pedido sin identificarse
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
    private long id(String email) {
        return usuarios.findByEmailIgnoreCase(email).orElseThrow().getId();
    }
    private Map<String, Object> datos() {
        int numero = secuencia.incrementAndGet();
        return new java.util.LinkedHashMap<>(Map.of("dni", numero, "nombre", "Cliente prueba",
                "direccion", "Av. Corrientes 123", "email", "cliente" + numero + "@example.com",
                "telefono", "1122334455", "contrasenia", "prueba123"));
    }
    private long crear(String token, String ruta, Map<String, Object> datos) throws Exception {
        var respuesta = enviar(token, "POST", ruta, datos);
        assertThat(respuesta.statusCode()).as(respuesta.body()).isEqualTo(201);
        return json.readTree(respuesta.body()).get("id").asLong();
    }
    private long rol(String nombre) {
        return roles.findByNombreIgnoreCase(nombre).orElseThrow().getId();
    }

    @Test
    void sinTokenOConTokenFalsoNoSePuedeUsarLaApiNiSuplantarAlUsuario() throws Exception {
        var respuesta = http.send(HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + "/api/personas"))
                .header("usuarioId", "1").header("X-User-Id", "1").GET().build(), HttpResponse.BodyHandlers.ofString());
        assertThat(respuesta.statusCode()).isEqualTo(401);
        assertThat(respuesta.body()).contains("\"error\"");
        assertThat(enviar(null, "GET", "/api/personas", null).statusCode()).isEqualTo(401);
        assertThat(enviar("no.es.un.token", "GET", "/api/personas", null).statusCode()).isEqualTo(401);
        // Un token de un usuario con la firma cambiada
        String token = login("ema@gmail.com", "cliente123");
        String alterado = token.substring(0, token.length() - 2) + (token.endsWith("AA") ? "BB" : "AA");
        assertThat(enviar(alterado, "GET", "/api/sesion", null).statusCode()).isEqualTo(401);
        for (String ruta : new String[] {"/%61pi/personas", "/api;parametro=1/personas"}) {
            assertThat(enviar(null, "GET", ruta, null).statusCode()).isIn(400, 401);
        }
    }

    @Test
    void lasVariantesCodificadasDeUnaRutaNoEludenLosPermisos() throws Exception {
        // El cliente no puede ver personas: ninguna variante de la ruta tiene que dejarlo pasar
        var cliente = login("ema@gmail.com", "cliente123");
        for (String ruta : new String[] {"/%61pi/personas", "/api;parametro=1/personas", "/api/personas/"}) {
            assertThat(enviar(cliente, "GET", ruta, null).statusCode()).as(ruta).isIn(400, 403, 404);
        }
    }

    @Test
    void elLoginDevuelveTokenYPermisosEfectivosSinLaContrasenia() throws Exception {
        var respuesta = enviar(null, "POST", "/api/login", Map.of("email", "pepe@sixt.com.ar", "contrasenia", "vendedor123"));
        var cuerpo = json.readTree(respuesta.body());
        assertThat(cuerpo.get("token").asText()).isNotBlank();
        assertThat(cuerpo.get("rol").asText()).isEqualTo("VENDEDOR");
        // RESERVA:ADMINISTRAR incluye por cascada RESERVA:VER y RESERVA:VER_PROPIAS
        assertThat(cuerpo.get("permisos").toString()).contains("RESERVA:VER\"", "RESERVA:VER_PROPIAS", "RESERVA:VENDER");
        assertThat(respuesta.body()).doesNotContain("contrasenia", "$2a$");
        assertThat(enviar(null, "POST", "/api/login", Map.of("email", "pepe@sixt.com.ar", "contrasenia", "mal")).statusCode())
                .isEqualTo(401);
    }

    @Test
    void vendedorDaDeAltaClientesSinElegirRolYNoPuedeAsignarOtro() throws Exception {
        var vendedor = login("pepe@sixt.com.ar", "vendedor123");
        var datos = datos();
        // Sin rol en el pedido: el servidor asigna CLIENTE
        long id = crear(vendedor, "/api/personas", datos);
        String email = (String) datos.get("email");
        // Si manda a mano un rol de empleado, se rechaza
        var conRol = datos();
        conRol.put("rolId", rol("ADMIN"));
        assertThat(enviar(vendedor, "POST", "/api/personas", conRol).statusCode()).isEqualTo(403);
        // Al editar sin rol se conserva CLIENTE; pasarlo a VENDEDOR se rechaza
        datos.put("direccion", "Av. Rivadavia 456");
        assertThat(enviar(vendedor, "PUT", "/api/personas/" + id, datos).statusCode()).isEqualTo(200);
        assertThat(personas.findById(id).orElseThrow().getDireccion()).isEqualTo("Av. Rivadavia 456");
        var aVendedor = new java.util.HashMap<String, Object>(datos);
        aVendedor.put("rolId", rol("VENDEDOR"));
        assertThat(enviar(vendedor, "PUT", "/api/personas/" + id, aVendedor).statusCode()).isEqualTo(403);
        // En la pantalla de personas el vendedor solo ve clientes
        var lista = json.readTree(enviar(vendedor, "GET", "/api/personas", null).body());
        assertThat(lista.toString()).contains(email).doesNotContain("admin@sixt.com.ar", "pepe@sixt.com.ar");
        for (var fila : lista) {
            assertThat(fila.get("rol").get("nombre").asText()).isEqualTo("CLIENTE");
            assertThat(fila.get("rol").get("permisos").size()).isEqualTo(2);
            assertThat(fila.has("usuario")).isFalse();
            assertThat(fila.has("contrasenia")).isFalse();
        }
        assertThat(enviar(vendedor, "DELETE", "/api/personas/" + id, null).statusCode()).isEqualTo(204);
        assertThat(enviar(null, "POST", "/api/login", Map.of("email", email, "contrasenia", "prueba123")).statusCode()).isEqualTo(401);
    }

    @Test
    void vendedorNoAdministraEmpleadosRolesNiCuentasPrivilegiadas() throws Exception {
        var vendedor = login("pepe@sixt.com.ar", "vendedor123");
        for (String ruta : new String[] {"/api/roles", "/api/permisos"}) {
            assertThat(enviar(vendedor, "GET", ruta, null).statusCode()).isEqualTo(403);
            assertThat(enviar(vendedor, "POST", ruta, datos()).statusCode()).isEqualTo(403);
        }
        for (long id : new long[] {id("admin@sixt.com.ar"), id("pepe@sixt.com.ar")}) {
            assertThat(enviar(vendedor, "PUT", "/api/personas/" + id, datos()).statusCode()).isEqualTo(403);
            assertThat(enviar(vendedor, "DELETE", "/api/personas/" + id, null).statusCode()).isEqualTo(403);
        }
        // Puede ver vehiculos y oficinas, pero no modificarlos
        assertThat(enviar(vendedor, "GET", "/api/vehiculos", null).statusCode()).isEqualTo(200);
        assertThat(enviar(vendedor, "POST", "/api/oficinas", Map.of("zona", "x", "direccion", "y")).statusCode()).isEqualTo(403);
    }

    @Test
    void clienteNoGestionaClientesYSoloConsultaSusReservas() throws Exception {
        var cliente = login("ema@gmail.com", "cliente123");
        long propio = id("ema@gmail.com");
        for (String metodo : new String[] {"GET", "POST", "PUT", "DELETE"}) {
            String ruta = metodo.equals("GET") || metodo.equals("POST") ? "/api/personas" : "/api/personas/1";
            assertThat(enviar(cliente, metodo, ruta, metodo.equals("POST") || metodo.equals("PUT") ? datos() : null).statusCode()).isEqualTo(403);
        }
        assertThat(enviar(cliente, "GET", "/api/reservas?clienteId=" + propio, null).statusCode()).isEqualTo(200);
        // @PreAuthorize: sin RESERVA:VER no puede listar todas ni las de otro
        var todas = enviar(cliente, "GET", "/api/reservas", null);
        assertThat(todas.statusCode()).isEqualTo(403);
        assertThat(todas.body()).contains("\"error\"");
        assertThat(enviar(cliente, "GET", "/api/reservas?clienteId=999999", null).statusCode()).isEqualTo(403);
    }

    @Test
    void administradorConservaGestionGeneralYDeClientes() throws Exception {
        var admin = login("admin@sixt.com.ar", "admin123");
        for (String ruta : new String[] {"/api/personas", "/api/roles", "/api/vehiculos", "/api/oficinas"}) {
            assertThat(enviar(admin, "GET", ruta, null).statusCode()).isEqualTo(200);
        }
        // El admin ve empleados y clientes en la misma pantalla
        var lista = enviar(admin, "GET", "/api/personas", null).body();
        assertThat(lista).contains("pepe@sixt.com.ar", "ema@gmail.com");
        long id = crear(admin, "/api/personas", datos());
        assertThat(enviar(admin, "DELETE", "/api/personas/" + id, null).statusCode()).isEqualTo(204);
        // ADMIN solo ve los roles, no los cambia
        assertThat(enviar(admin, "POST", "/api/roles", Map.of("nombre", "X", "permisos", List.of("OFICINA:VER"))).statusCode())
                .isEqualTo(403);
    }

    @Test
    void validacionesYBajaConReservasPendientesSiguenVigentes() throws Exception {
        var vendedor = login("pepe@sixt.com.ar", "vendedor123");
        var datos = datos();
        long id = crear(vendedor, "/api/personas", datos);
        assertThat(enviar(vendedor, "POST", "/api/personas", datos).statusCode()).isEqualTo(409);
        datos.put("direccion", " ");
        assertThat(enviar(vendedor, "PUT", "/api/personas/" + id, datos).statusCode()).isEqualTo(400);
        long vendedorId = id("pepe@sixt.com.ar");
        var vehiculos = json.readTree(enviar(vendedor, "GET", "/api/vehiculos", null).body());
        var oficinas = json.readTree(enviar(vendedor, "GET", "/api/oficinas", null).body());
        long oficinaId = oficinas.get(0).get("id").asLong();
        var reserva = Map.of("clienteId", id, "vendedorId", vendedorId, "oficinaOrigenId", oficinaId,
                "oficinaDestinoId", oficinaId, "fechaDesde", LocalDate.now().plusDays(30).toString(),
                "fechaHasta", LocalDate.now().plusDays(31).toString(), "detalles",
                List.of(Map.of("vehiculoId", vehiculos.get(0).get("id").asLong(), "litrosNafta", 20)));
        var respuesta = enviar(vendedor, "POST", "/api/reservas", reserva);
        assertThat(respuesta.statusCode()).as(respuesta.body()).isEqualTo(201);
        long reservaId = json.readTree(respuesta.body()).get("id").asLong();
        // Un cliente no cancela reservas ajenas
        var otroCliente = login("ema@gmail.com", "cliente123");
        assertThat(enviar(otroCliente, "POST", "/api/reservas/" + reservaId + "/cancelar", Map.of()).statusCode()).isEqualTo(403);
        assertThat(enviar(vendedor, "DELETE", "/api/personas/" + id, null).statusCode()).isEqualTo(409);
        assertThat(enviar(vendedor, "DELETE", "/api/reservas/" + reservaId, null).statusCode()).isEqualTo(403);
        assertThat(enviar(vendedor, "POST", "/api/reservas/" + reservaId + "/devolver", Map.of()).statusCode()).isEqualTo(200);
        assertThat(enviar(vendedor, "DELETE", "/api/personas/" + id, null).statusCode()).isEqualTo(204);
    }

    @Test
    void elClienteReservaOnlineSoloParaSiMismoYSinVendedor() throws Exception {
        var cliente = login("ema@gmail.com", "cliente123");
        long propio = id("ema@gmail.com");
        String desde = LocalDate.now().plusDays(60).toString();
        String hasta = LocalDate.now().plusDays(61).toString();
        var oficinas = json.readTree(enviar(cliente, "GET", "/api/oficinas", null).body());
        long oficinaId = oficinas.get(0).get("id").asLong();
        var disponibles = enviar(cliente, "GET", "/api/vehiculos/disponibles?desde=" + desde + "&hasta=" + hasta
                + "&oficinaOrigenId=" + oficinaId + "&oficinaDestinoId=" + oficinaId, null);
        assertThat(disponibles.statusCode()).as(disponibles.body()).isEqualTo(200);
        long vehiculoId = json.readTree(disponibles.body()).get(0).get("id").asLong();
        var detalles = List.of(Map.of("vehiculoId", vehiculoId, "litrosNafta", 10));

        var paraOtro = new java.util.HashMap<String, Object>(Map.of("clienteId", id("pepe@sixt.com.ar"),
                "oficinaOrigenId", oficinaId, "oficinaDestinoId", oficinaId,
                "fechaDesde", desde, "fechaHasta", hasta, "detalles", detalles));
        assertThat(enviar(cliente, "POST", "/api/reservas", paraOtro).statusCode()).isEqualTo(403);
        var conVendedor = new java.util.HashMap<String, Object>(paraOtro);
        conVendedor.put("clienteId", propio);
        conVendedor.put("vendedorId", id("pepe@sixt.com.ar"));
        assertThat(enviar(cliente, "POST", "/api/reservas", conVendedor).statusCode()).isEqualTo(403);
        var online = new java.util.HashMap<String, Object>(paraOtro);
        online.put("clienteId", propio);
        var respuesta = enviar(cliente, "POST", "/api/reservas", online);
        assertThat(respuesta.statusCode()).as(respuesta.body()).isEqualTo(201);
        // Puede cancelar su propia reserva
        long reservaId = json.readTree(respuesta.body()).get("id").asLong();
        var cancelada = enviar(cliente, "POST", "/api/reservas/" + reservaId + "/cancelar", Map.of());
        assertThat(cancelada.statusCode()).as(cancelada.body()).isEqualTo(200);

        // Ve el catalogo de vehiculos solo a traves del buscador
        assertThat(enviar(cliente, "GET", "/api/vehiculos", null).statusCode()).isEqualTo(403);
        assertThat(enviar(cliente, "GET", "/api/modelos", null).statusCode()).isEqualTo(403);
    }

    @Test
    void elCatalogoDeModelosEsParteDelRecursoVehiculo() throws Exception {
        var vendedor = login("pepe@sixt.com.ar", "vendedor123");
        var admin = login("admin@sixt.com.ar", "admin123");
        assertThat(enviar(vendedor, "GET", "/api/modelos", null).statusCode()).isEqualTo(200);
        assertThat(enviar(vendedor, "GET", "/api/tipos", null).statusCode()).isEqualTo(200);
        assertThat(enviar(vendedor, "POST", "/api/tipos", Map.of("nombre", "VAN", "precio", 20000)).statusCode()).isEqualTo(403);
        assertThat(enviar(admin, "GET", "/api/tipos", null).statusCode()).isEqualTo(200);
    }

    @Test
    void logoutInvalidaElTokenYLosTokensSonIndependientes() throws Exception {
        var vendedor = login("pepe@sixt.com.ar", "vendedor123");
        var cliente = login("ema@gmail.com", "cliente123");
        assertThat(enviar(cliente, "GET", "/api/personas", null).statusCode()).isEqualTo(403);
        assertThat(enviar(vendedor, "GET", "/api/personas", null).statusCode()).isEqualTo(200);
        assertThat(enviar(vendedor, "POST", "/api/logout", Map.of()).statusCode()).isEqualTo(204);
        assertThat(enviar(vendedor, "GET", "/api/personas", null).statusCode()).isEqualTo(401);
        assertThat(enviar(cliente, "GET", "/api/sesion", null).statusCode()).isEqualTo(200);
        // Despues de cerrar sesion puede volver a entrar
        assertThat(enviar(login("pepe@sixt.com.ar", "vendedor123"), "GET", "/api/personas", null).statusCode()).isEqualTo(200);
    }

    @Test
    void cadaUnoCambiaSoloSuPropiaContrasenia() throws Exception {
        var vendedor = login("pepe@sixt.com.ar", "vendedor123");
        // Ya no existe un endpoint para cambiar la contraseña de otro: el id sale siempre del token
        assertThat(enviar(vendedor, "PATCH", "/api/personas/" + id("admin@sixt.com.ar") + "/contrasenia",
                Map.of("contraseniaActual", "admin123", "contraseniaNueva", "cambio123")).statusCode()).isEqualTo(404);
        assertThat(enviar(vendedor, "PATCH", "/api/cuenta/contrasenia",
                Map.of("contraseniaActual", "incorrecta", "contraseniaNueva", "cambio123")).statusCode()).isEqualTo(400);
    }

    @Test
    void laSesionDevuelveLosDatosDeLaPersonaYSusPermisos() throws Exception {
        var cuerpo = json.readTree(enviar(login("ema@gmail.com", "cliente123"), "GET", "/api/sesion", null).body());
        assertThat(cuerpo.get("nombre").asText()).isEqualTo("Ema Morano");
        assertThat(cuerpo.get("email").asText()).isEqualTo("ema@gmail.com");
        assertThat(cuerpo.get("permisos").toString()).contains("RESERVA:ALQUILAR", "RESERVA:VER_PROPIAS");
    }

    @Test
    void revocarPermisoSeAplicaAlTokenExistente() throws Exception {
        var vendedor = login("pepe@sixt.com.ar", "vendedor123");
        var sysadmin = login("sysadmin@sixt.com.ar", "sysadmin123");
        long rolId = rol("VENDEDOR");
        var original = Map.of("nombre", "VENDEDOR", "permisos", List.of("RESERVA:ADMINISTRAR", "RESERVA:VENDER",
                "CLIENTE:ADMINISTRAR", "VEHICULO:VER", "OFICINA:VER"));
        var sinClientes = Map.of("nombre", "VENDEDOR", "permisos", List.of("RESERVA:ADMINISTRAR", "RESERVA:VENDER",
                "VEHICULO:VER", "OFICINA:VER"));
        try {
            assertThat(enviar(sysadmin, "PUT", "/api/roles/" + rolId, sinClientes).statusCode()).isEqualTo(200);
            assertThat(enviar(vendedor, "GET", "/api/personas", null).statusCode()).isEqualTo(403);
        } finally {
            assertThat(enviar(sysadmin, "PUT", "/api/roles/" + rolId, original).statusCode()).isEqualTo(200);
        }
        assertThat(enviar(vendedor, "GET", "/api/personas", null).statusCode()).isEqualTo(200);
    }
}
