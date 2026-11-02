package com.sixt;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

// Reservas conocidas en 2028 y el reporte de ese año, numero por numero.
// Precios del seed: Peugeot 206 (AUTO) $10.000 por dia, Toyota Hilux (CAMIONETA) $18.000 por dia.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:reportes-api;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa", "spring.datasource.password=", "spring.jpa.show-sql=false"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReportesApiTest {

    @LocalServerPort int puerto;
    @Autowired ObjectMapper json;
    private final HttpClient http = HttpClient.newHttpClient();

    private String admin;
    private JsonNode reporte2028;

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

    private JsonNode fila(JsonNode lista, String descripcion) {
        for (JsonNode fila : lista) {
            if (fila.get("descripcion").asText().equals(descripcion)) {
                return fila;
            }
        }
        throw new AssertionError("No hay fila " + descripcion);
    }

    private long reservar(String token, Long vendedorId, long clienteId, long vehiculoId, long retiro,
                          long devolucion, String desde, String hasta) throws Exception {
        Map<String, Object> datos = new HashMap<>();
        datos.put("clienteId", clienteId);
        datos.put("vendedorId", vendedorId);
        datos.put("oficinaOrigenId", retiro);
        datos.put("oficinaDestinoId", devolucion);
        datos.put("fechaDesde", desde);
        datos.put("fechaHasta", hasta);
        datos.put("detalles", List.of(Map.of("vehiculoId", vehiculoId, "litrosNafta", 10)));
        var respuesta = enviar(token, "POST", "/api/reservas", datos);
        assertThat(respuesta.statusCode()).as(respuesta.body()).isEqualTo(201);
        return json.readTree(respuesta.body()).get("id").asLong();
    }

    @BeforeAll
    void cargarReservasDe2028() throws Exception {
        admin = login("admin@sixt.com.ar", "admin123");
        String vendedor = login("pepe@sixt.com.ar", "vendedor123");
        String cliente = login("ema@gmail.com", "cliente123");
        long pepe = json.readTree(enviar(vendedor, "GET", "/api/sesion", null).body()).get("id").asLong();
        long ema = json.readTree(enviar(cliente, "GET", "/api/sesion", null).body()).get("id").asLong();
        JsonNode oficinas = json.readTree(enviar(admin, "GET", "/api/oficinas", null).body());
        long ofi1 = idDe(oficinas, "zona", "Buenos Aires");
        long ofi2 = idDe(oficinas, "zona", "Córdoba");
        JsonNode vehiculos = json.readTree(enviar(admin, "GET", "/api/vehiculos", null).body());
        long peugeot = idDe(vehiculos, "patente", "APG023");
        long hilux = idDe(vehiculos, "patente", "AE123CD");

        // Pepe vende un 206 de Buenos Aires a Córdoba, 3 dias de marzo: $30.000
        reservar(vendedor, pepe, ema, peugeot, ofi1, ofi2, "2028-03-10", "2028-03-12");
        // Ema reserva online la Hilux en Buenos Aires, 2 dias de abril: $36.000
        reservar(cliente, null, ema, hilux, ofi1, ofi1, "2028-04-01", "2028-04-02");
        // Una reserva de mayo que se cancela: no se factura, solo se cuenta
        long cancelada = reservar(vendedor, pepe, ema, hilux, ofi1, ofi1, "2028-05-01", "2028-05-01");
        assertThat(enviar(vendedor, "POST", "/api/reservas/" + cancelada + "/cancelar", Map.of()).statusCode())
                .isEqualTo(200);

        var respuesta = enviar(admin, "GET", "/api/reportes?desde=2028-01-01&hasta=2028-12-31", null);
        assertThat(respuesta.statusCode()).as(respuesta.body()).isEqualTo(200);
        reporte2028 = json.readTree(respuesta.body());
    }

    @Test
    void losIndicadoresDelPeriodo() {
        assertThat(reporte2028.get("facturado").decimalValue()).isEqualByComparingTo("66000");
        assertThat(reporte2028.get("reservas").asInt()).isEqualTo(2);
        assertThat(reporte2028.get("promedio").decimalValue()).isEqualByComparingTo("33000");
        assertThat(reporte2028.get("canceladas").asLong()).isEqualTo(1);
        assertThat(reporte2028.get("diasAlquilado").asLong()).isEqualTo(5);
        // 5 dias alquilados sobre 6 autos x 366 dias: 0,23 %, redondea a 0
        assertThat(reporte2028.get("porVehiculo").size()).isEqualTo(6);
        assertThat(reporte2028.get("ocupacion").asInt()).isZero();
    }

    @Test
    void porVehiculoOrdenadoDeMayorAMenorFacturacion() {
        JsonNode filas = reporte2028.get("porVehiculo");
        assertThat(filas.get(0).get("descripcion").asText()).isEqualTo("AE123CD");
        assertThat(filas.get(0).get("modelo").asText()).isEqualTo("Toyota Hilux");
        assertThat(filas.get(0).get("facturado").decimalValue()).isEqualByComparingTo("36000");
        assertThat(filas.get(0).get("diasAlquilado").asLong()).isEqualTo(2);
        assertThat(filas.get(1).get("descripcion").asText()).isEqualTo("APG023");
        assertThat(filas.get(1).get("reservas").asInt()).isEqualTo(1);
    }

    @Test
    void porOficinaCuentaRetirosYDevoluciones() {
        JsonNode ofi1 = fila(reporte2028.get("porOficina"), "Buenos Aires");
        assertThat(ofi1.get("reservas").asInt()).isEqualTo(2);
        assertThat(ofi1.get("facturado").decimalValue()).isEqualByComparingTo("66000");
        assertThat(ofi1.get("devoluciones").asInt()).isEqualTo(1);
        JsonNode ofi2 = fila(reporte2028.get("porOficina"), "Córdoba");
        assertThat(ofi2.get("reservas").asInt()).isZero();
        assertThat(ofi2.get("devoluciones").asInt()).isEqualTo(1);
    }

    @Test
    void porVendedorConLasOnlineAparte() {
        JsonNode filas = reporte2028.get("porVendedor");
        assertThat(filas.size()).isEqualTo(2);
        assertThat(fila(filas, "Reservas online (sin vendedor)").get("facturado").decimalValue())
                .isEqualByComparingTo("36000");
        assertThat(fila(filas, "Pepe Vendedor").get("facturado").decimalValue()).isEqualByComparingTo("30000");
    }

    @Test
    void porMesConLaBarraProporcionalAlMejorMes() {
        JsonNode meses = reporte2028.get("porMes");
        assertThat(meses.size()).isEqualTo(12);
        assertThat(meses.get(2).get("descripcion").asText()).isEqualTo("marzo 2028");
        assertThat(meses.get(2).get("porcentajeBarra").asInt()).isEqualTo(83);
        assertThat(meses.get(3).get("porcentajeBarra").asInt()).isEqualTo(100);
        assertThat(meses.get(4).get("facturado").decimalValue()).isEqualByComparingTo("0");
    }

    @Test
    void unPeriodoMasCortoSoloTomaLoQueEmpiezaEnEl() throws Exception {
        JsonNode marzo = json.readTree(enviar(admin, "GET", "/api/reportes?desde=2028-03-01&hasta=2028-03-31", null).body());
        assertThat(marzo.get("facturado").decimalValue()).isEqualByComparingTo("30000");
        assertThat(marzo.get("porMes").size()).isEqualTo(1);
    }

    @Test
    void unPeriodoAlRevesEsUnError() throws Exception {
        var respuesta = enviar(admin, "GET", "/api/reportes?desde=2028-12-31&hasta=2028-01-01", null);
        assertThat(respuesta.statusCode()).isEqualTo(400);
    }
}
