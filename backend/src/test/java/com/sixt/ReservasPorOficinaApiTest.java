package com.sixt;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

// De punta a punta: una reserva de Buenos Aires a Córdoba deja el auto en Córdoba para las siguientes
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:reservas-oficina;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa", "spring.datasource.password=", "spring.jpa.show-sql=false"})
class ReservasPorOficinaApiTest {

    @LocalServerPort int puerto;
    @Autowired ObjectMapper json;
    private final HttpClient http = HttpClient.newHttpClient();

    private HttpResponse<String> enviar(String token, String metodo, String ruta, Object datos) throws Exception {
        var cuerpo = datos == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(json.writeValueAsString(datos));
        var pedido = HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + ruta))
                .header("Content-Type", "application/json").method(metodo, cuerpo);
        if (token != null) {
            pedido.header("Authorization", "Bearer " + token);
        }
        return http.send(pedido.build(), HttpResponse.BodyHandlers.ofString());
    }

    private String login(String email, String clave) throws Exception {
        var respuesta = enviar(null, "POST", "/api/login", Map.of("email", email, "contrasenia", clave));
        return json.readTree(respuesta.body()).get("token").asText();
    }

    private long idDe(JsonNode lista, String campo, String valor) {
        for (JsonNode fila : lista) {
            if (fila.get(campo).asText().equals(valor)) {
                return fila.get("id").asLong();
            }
        }
        throw new AssertionError("No se encontro " + valor);
    }

    private List<Long> disponibles(String token, LocalDate desde, LocalDate hasta, long retiro, long devolucion)
            throws Exception {
        var respuesta = enviar(token, "GET", "/api/vehiculos/disponibles?desde=" + desde + "&hasta=" + hasta
                + "&oficinaOrigenId=" + retiro + "&oficinaDestinoId=" + devolucion, null);
        assertThat(respuesta.statusCode()).as(respuesta.body()).isEqualTo(200);
        List<Long> ids = new java.util.ArrayList<>();
        for (JsonNode vehiculo : json.readTree(respuesta.body())) {
            ids.add(vehiculo.get("id").asLong());
        }
        return ids;
    }

    private HttpResponse<String> reservar(String token, long vendedorId, long clienteId, long vehiculoId,
                                          long retiro, long devolucion, LocalDate desde, LocalDate hasta)
            throws Exception {
        Map<String, Object> datos = new HashMap<>();
        datos.put("clienteId", clienteId);
        datos.put("vendedorId", vendedorId);
        datos.put("oficinaOrigenId", retiro);
        datos.put("oficinaDestinoId", devolucion);
        datos.put("fechaDesde", desde.toString());
        datos.put("fechaHasta", hasta.toString());
        datos.put("detalles", List.of(Map.of("vehiculoId", vehiculoId, "litrosNafta", 10)));
        return enviar(token, "POST", "/api/reservas", datos);
    }

    @Test
    void elAutoQuedaEnLaOficinaDeDevolucionParaLasReservasSiguientes() throws Exception {
        String vendedor = login("pepe@sixt.com.ar", "vendedor123");
        long vendedorId = json.readTree(enviar(vendedor, "GET", "/api/sesion", null).body()).get("id").asLong();
        long clienteId = idDe(json.readTree(enviar(vendedor, "GET", "/api/personas", null).body()), "email", "ema@gmail.com");
        JsonNode oficinas = json.readTree(enviar(vendedor, "GET", "/api/oficinas", null).body());
        long ofi1 = idDe(oficinas, "zona", "Buenos Aires");
        long ofi2 = idDe(oficinas, "zona", "Córdoba");
        long auto = idDe(json.readTree(enviar(vendedor, "GET", "/api/vehiculos", null).body()), "patente", "APG023");

        LocalDate dia10 = LocalDate.now().plusDays(40);
        LocalDate dia15 = dia10.plusDays(5);
        LocalDate dia16 = dia15.plusDays(1);
        LocalDate dia18 = dia15.plusDays(3);

        // Hoy el auto esta en Buenos Aires: se ofrece ahi y no en Córdoba
        assertThat(disponibles(vendedor, dia10, dia15, ofi1, ofi2)).contains(auto);
        assertThat(disponibles(vendedor, dia10, dia15, ofi2, ofi2)).doesNotContain(auto);

        // Se reserva de Buenos Aires a Córdoba, del 10 al 15
        var respuesta = reservar(vendedor, vendedorId, clienteId, auto, ofi1, ofi2, dia10, dia15);
        assertThat(respuesta.statusCode()).as(respuesta.body()).isEqualTo(201);

        // Desde el 16 se puede retirar en Córdoba, no en Buenos Aires
        assertThat(disponibles(vendedor, dia16, dia18, ofi2, ofi2)).contains(auto);
        assertThat(disponibles(vendedor, dia16, dia18, ofi1, ofi1)).doesNotContain(auto);
        // El dia de la devolucion todavia esta ocupado
        assertThat(disponibles(vendedor, dia15, dia18, ofi2, ofi2)).doesNotContain(auto);

        // El alta valida lo mismo aunque alguien no use el buscador
        var desdeOfi1 = reservar(vendedor, vendedorId, clienteId, auto, ofi1, ofi1, dia16, dia18);
        assertThat(desdeOfi1.statusCode()).isEqualTo(409);
        assertThat(desdeOfi1.body()).contains("Córdoba");

        // Antes del 10 el auto esta en Buenos Aires, y hay que devolverlo ahi para la reserva del 10
        LocalDate dia5 = dia10.minusDays(5);
        LocalDate dia7 = dia10.minusDays(3);
        assertThat(disponibles(vendedor, dia5, dia7, ofi1, ofi1)).contains(auto);
        assertThat(disponibles(vendedor, dia5, dia7, ofi1, ofi2)).doesNotContain(auto);
    }
}
