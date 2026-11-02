package com.sixt.frontend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

// El token de la API (JWT) se guarda en la sesion de cada usuario, nunca en el cliente HTTP global,
// y viaja en cada pedido como "Authorization: Bearer <token>".
public class ClienteBackend {
    private static final String RUTA_BACKEND = System.getenv().getOrDefault("BACKEND_URL", "http://localhost:8000");
    private static final HttpClient CLIENTE = HttpClient.newHttpClient();
    private static final ObjectMapper JSON = new ObjectMapper();
    private ClienteBackend() { }

    public record Respuesta(int estado, Map<String, Object> cuerpo) {
        public boolean ok() { return estado >= 200 && estado < 300; }
        public String error() {
            Object error = cuerpo.get("error");
            return error == null ? "Error del backend (HTTP " + estado + ")" : error.toString();
        }
    }

    public static List<Map<String, Object>> getLista(HttpServletRequest request, String ruta) throws ServletException {
        return JSON.convertValue(get(request, ruta), new TypeReference<List<Map<String, Object>>>() {});
    }
    public static Map<String, Object> getObjeto(HttpServletRequest request, String ruta) throws ServletException {
        return JSON.convertValue(get(request, ruta), new TypeReference<Map<String, Object>>() {});
    }
    public static Respuesta consultarSesion(HttpServletRequest request) throws ServletException {
        return enviar(request, "GET", "/api/sesion", null);
    }
    public static Respuesta post(HttpServletRequest request, String ruta, Map<String, Object> datos) throws ServletException {
        return enviar(request, "POST", ruta, datos);
    }
    public static Respuesta put(HttpServletRequest request, String ruta, Map<String, Object> datos) throws ServletException {
        return enviar(request, "PUT", ruta, datos);
    }
    public static Respuesta patch(HttpServletRequest request, String ruta, Map<String, Object> datos) throws ServletException {
        return enviar(request, "PATCH", ruta, datos);
    }
    public static Respuesta delete(HttpServletRequest request, String ruta) throws ServletException {
        return enviar(request, "DELETE", ruta, null);
    }
    private static HttpRequest.Builder peticion(HttpServletRequest request, String ruta) {
        var builder = HttpRequest.newBuilder().uri(URI.create(RUTA_BACKEND + ruta));
        var sesion = request.getSession(false);
        if (!ruta.equals("/api/login") && sesion != null && sesion.getAttribute("tokenBackend") instanceof String token) {
            builder.header("Authorization", "Bearer " + token);
        }
        return builder;
    }
    private static Respuesta enviar(HttpServletRequest request, String metodo, String ruta, Map<String, Object> datos)
            throws ServletException {
        try {
            var cuerpo = datos == null ? HttpRequest.BodyPublishers.noBody()
                    : HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(datos));
            var respuesta = CLIENTE.send(peticion(request, ruta).header("Content-Type", "application/json")
                    .method(metodo, cuerpo).build(), HttpResponse.BodyHandlers.ofString());
            Map<String, Object> mapa = respuesta.body() == null || respuesta.body().isBlank() ? Map.of()
                    : JSON.readValue(respuesta.body(), new TypeReference<Map<String, Object>>() {});
            return new Respuesta(respuesta.statusCode(), mapa);
        } catch (Exception e) {
            throw new ServletException("No se pudo comunicar con el backend en " + RUTA_BACKEND, e);
        }
    }
    private static Object get(HttpServletRequest request, String ruta) throws ServletException {
        try {
            var respuesta = CLIENTE.send(peticion(request, ruta).GET().build(), HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() < 200 || respuesta.statusCode() >= 300) {
                throw new ServletException("El backend rechazo la consulta (HTTP " + respuesta.statusCode() + ")");
            }
            return JSON.readValue(respuesta.body(), Object.class);
        } catch (Exception e) {
            throw new ServletException("No se pudo consultar el backend en " + RUTA_BACKEND, e);
        }
    }
}
