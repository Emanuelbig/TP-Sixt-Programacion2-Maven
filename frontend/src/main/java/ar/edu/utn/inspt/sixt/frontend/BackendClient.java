package ar.edu.utn.inspt.sixt.frontend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

// Unico lugar que sabe hablar con el BACKEND.
// Este servidor actua como CLIENTE de otro servidor (igual que en biblioteca).
public class BackendClient {

    // En Docker se pisa con la variable BACKEND_URL (http://backend:8000)
    private static final String RUTA_BACKEND =
            System.getenv().getOrDefault("BACKEND_URL", "http://localhost:8000");

    private static final HttpClient CLIENTE = HttpClient.newHttpClient();
    private static final ObjectMapper JSON = new ObjectMapper();

    // Respuesta de un POST: el codigo HTTP y el JSON ya convertido a mapa
    public record Respuesta(int status, Map<String, Object> body) {
        public boolean ok() {
            return status >= 200 && status < 300;
        }

        public String error() {
            Object error = body.get("error");
            return error != null ? error.toString() : "Error del backend (HTTP " + status + ")";
        }
    }

    public static List<Map<String, Object>> getLista(String ruta) throws ServletException {
        return JSON.convertValue(get(ruta), new TypeReference<List<Map<String, Object>>>() {});
    }

    public static Map<String, Object> getObjeto(String ruta) throws ServletException {
        return JSON.convertValue(get(ruta), new TypeReference<Map<String, Object>>() {});
    }

    public static Respuesta post(String ruta, Map<String, Object> datos) throws ServletException {
        try {
            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(URI.create(RUTA_BACKEND + ruta))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(datos)))
                    .build();

            HttpResponse<String> respuesta = CLIENTE.send(peticion, HttpResponse.BodyHandlers.ofString());
            String cuerpo = respuesta.body();
            Map<String, Object> mapa = cuerpo == null || cuerpo.isBlank()
                    ? Map.of()
                    : JSON.readValue(cuerpo, new TypeReference<Map<String, Object>>() {});
            return new Respuesta(respuesta.statusCode(), mapa);
        } catch (Exception e) {
            throw new ServletException("No se pudo comunicar con el backend en " + RUTA_BACKEND, e);
        }
    }

    private static Object get(String ruta) throws ServletException {
        try {
            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(URI.create(RUTA_BACKEND + ruta))
                    .GET()
                    .build();

            HttpResponse<String> respuesta = CLIENTE.send(peticion, HttpResponse.BodyHandlers.ofString());
            return JSON.readValue(respuesta.body(), Object.class);
        } catch (Exception e) {
            throw new ServletException("No se pudo comunicar con el backend en " + RUTA_BACKEND, e);
        }
    }
}
