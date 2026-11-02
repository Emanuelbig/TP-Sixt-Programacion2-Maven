package com.sixt.seguridad;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

// La prueba manual de "entrar con cada usuario y ver que puede hacer", automatizada:
// cada fila es un usuario de prueba, un pedido y la respuesta esperada.
// Las escrituras denegadas se cortan en el filtro (403) antes de leer el cuerpo.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:recorrido;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa", "spring.datasource.password=", "spring.jpa.show-sql=false"})
class RecorridoDeUsuariosTest {

    private static final Map<String, String> CLAVES = Map.of(
            "sysadmin@sixt.com.ar", "sysadmin123",
            "admin@sixt.com.ar", "admin123",
            "pepe@sixt.com.ar", "vendedor123",
            "ema@gmail.com", "cliente123");

    // Un token por usuario para todo el test (el login con BCrypt es lento)
    private static final Map<String, String> TOKENS = new HashMap<>();

    @LocalServerPort int puerto;
    @Autowired ObjectMapper json;
    private final HttpClient http = HttpClient.newHttpClient();

    private String token(String email) throws Exception {
        String token = TOKENS.get(email);
        if (token == null) {
            String cuerpo = json.writeValueAsString(Map.of("email", email, "contrasenia", CLAVES.get(email)));
            HttpResponse<String> respuesta = http.send(HttpRequest.newBuilder(URI.create(url("/api/login")))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpo)).build(), HttpResponse.BodyHandlers.ofString());
            assertThat(respuesta.statusCode()).as(respuesta.body()).isEqualTo(200);
            token = json.readTree(respuesta.body()).get("token").asText();
            TOKENS.put(email, token);
        }
        return token;
    }

    private HttpResponse<String> enviar(String email, String metodo, String ruta) throws Exception {
        HttpRequest pedido = HttpRequest.newBuilder(URI.create(url(ruta)))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token(email))
                .method(metodo, HttpRequest.BodyPublishers.ofString("{}"))
                .build();
        return http.send(pedido, HttpResponse.BodyHandlers.ofString());
    }

    private String url(String ruta) {
        return "http://localhost:" + puerto + ruta;
    }

    @ParameterizedTest(name = "{0} {1} {2} -> {3}")
    @CsvSource({
            // Sysadmin: administra todo, incluidos los roles
            "sysadmin@sixt.com.ar, GET,    /api/roles,      200",
            "sysadmin@sixt.com.ar, GET,    /api/permisos,   200",
            "sysadmin@sixt.com.ar, GET,    /api/personas,   200",
            "sysadmin@sixt.com.ar, GET,    /api/reservas,   200",
            "sysadmin@sixt.com.ar, GET,    /api/tipos,      200",
            // Admin: administra la plataforma, ve los roles pero no los cambia
            "admin@sixt.com.ar,    GET,    /api/oficinas,   200",
            "admin@sixt.com.ar,    GET,    /api/vehiculos,  200",
            "admin@sixt.com.ar,    GET,    /api/modelos,    200",
            "admin@sixt.com.ar,    GET,    /api/personas,   200",
            "admin@sixt.com.ar,    GET,    /api/reservas,   200",
            "admin@sixt.com.ar,    GET,    /api/roles,      200",
            "admin@sixt.com.ar,    GET,    /api/permisos,   200",
            "admin@sixt.com.ar,    POST,   /api/roles,      403",
            "admin@sixt.com.ar,    PUT,    /api/roles/1,    403",
            "admin@sixt.com.ar,    DELETE, /api/roles/1,    403",
            "admin@sixt.com.ar,    GET,    /api/reportes,          200",
            // Vendedor: reservas y clientes; ve vehiculos, catalogo y oficinas
            "pepe@sixt.com.ar,     GET,    /api/reservas,   200",
            "pepe@sixt.com.ar,     GET,    /api/personas,   200",
            "pepe@sixt.com.ar,     GET,    /api/vehiculos,  200",
            "pepe@sixt.com.ar,     GET,    /api/modelos,    200",
            "pepe@sixt.com.ar,     GET,    /api/oficinas,   200",
            "pepe@sixt.com.ar,     DELETE, /api/personas/1, 403",
            "pepe@sixt.com.ar,     GET,    /api/roles,      403",
            "pepe@sixt.com.ar,     POST,   /api/vehiculos,  403",
            "pepe@sixt.com.ar,     PUT,    /api/modelos/1,  403",
            "pepe@sixt.com.ar,     POST,   /api/oficinas,   403",
            "pepe@sixt.com.ar,     DELETE, /api/reservas/1, 403",
            "pepe@sixt.com.ar,     DELETE, /api/oficinas/1, 403",
            "pepe@sixt.com.ar,     DELETE, /api/modelos/1,  403",
            "pepe@sixt.com.ar,     GET,    /api/reportes,          403",
            // Cliente: solo el buscador, sus reservas y su cuenta
            "ema@gmail.com,        GET,    /api/oficinas,   200",
            "ema@gmail.com,        GET,    /api/sesion,     200",
            "ema@gmail.com,        GET,    /api/resumen,    200",
            "ema@gmail.com,        GET,    /api/reservas,   403",
            "ema@gmail.com,        GET,    /api/vehiculos,  403",
            "ema@gmail.com,        POST,   /api/personas,   403",
            "ema@gmail.com,        GET,    /api/personas,   403",
            "ema@gmail.com,        GET,    /api/roles,      403",
            "ema@gmail.com,        POST,   /api/oficinas,   403",
            "ema@gmail.com,        POST,   /api/reservas/1/devolver, 403",
            "ema@gmail.com,        GET,    /api/reportes,          403",
            "sysadmin@sixt.com.ar, GET,    /api/reportes,          200"
    })
    void cadaUsuarioDePruebaAccedeSoloALoQueLeCorresponde(String email, String metodo, String ruta, int estado)
            throws Exception {
        HttpResponse<String> respuesta = enviar(email, metodo, ruta);
        assertThat(respuesta.statusCode()).as(respuesta.body()).isEqualTo(estado);
        if (estado == 403) {
            assertThat(respuesta.body()).contains("\"error\"");
        }
    }

    // Los menus del frontend se arman con estos permisos (ver PermisosDePantalla en el frontend)
    @ParameterizedTest(name = "{0} tiene {1}: {2}")
    @CsvSource({
            "sysadmin@sixt.com.ar, ROL:ADMINISTRAR,   true",
            "sysadmin@sixt.com.ar, ROL:ELIMINAR,      true",
            "sysadmin@sixt.com.ar, RESERVA:VENDER,    false",
            "admin@sixt.com.ar,    OFICINA:VER,       true",
            "admin@sixt.com.ar,    VEHICULO:ELIMINAR, true",
            "admin@sixt.com.ar,    USUARIO:VER,       true",
            "admin@sixt.com.ar,    ROL:VER,           true",
            "admin@sixt.com.ar,    ROL:CREAR,         false",
            "admin@sixt.com.ar,    OFICINA:ELIMINAR,  true",
            "admin@sixt.com.ar,    REPORTE:VER,       true",
            "sysadmin@sixt.com.ar, REPORTE:VER,       true",
            "pepe@sixt.com.ar,     REPORTE:VER,       false",
            "pepe@sixt.com.ar,     RESERVA:VER,       true",
            "pepe@sixt.com.ar,     RESERVA:EDITAR,    true",
            "pepe@sixt.com.ar,     RESERVA:VENDER,    true",
            "pepe@sixt.com.ar,     CLIENTE:ELIMINAR,  true",
            "pepe@sixt.com.ar,     VEHICULO:VER,      true",
            "pepe@sixt.com.ar,     VEHICULO:CREAR,    false",
            "pepe@sixt.com.ar,     USUARIO:VER,       false",
            "pepe@sixt.com.ar,     RESERVA:ALQUILAR,  false",
            "ema@gmail.com,        RESERVA:ALQUILAR,  true",
            "ema@gmail.com,        RESERVA:VER_PROPIAS, true",
            "ema@gmail.com,        RESERVA:VER,       false",
            "ema@gmail.com,        OFICINA:VER,       false"
    })
    void laSesionDevuelveLosPermisosConLosQueSeArmanLosMenus(String email, String permiso, boolean esperado)
            throws Exception {
        HttpResponse<String> respuesta = enviar(email, "GET", "/api/sesion");
        assertThat(respuesta.statusCode()).isEqualTo(200);
        boolean tiene = false;
        for (JsonNode codigo : json.readTree(respuesta.body()).get("permisos")) {
            if (codigo.asText().equals(permiso)) {
                tiene = true;
            }
        }
        assertThat(tiene).isEqualTo(esperado);
    }
}
