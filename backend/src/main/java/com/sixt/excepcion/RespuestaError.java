package com.sixt.excepcion;

// Cuerpo JSON de cualquier error de la API: {"error": "mensaje"}
public record RespuestaError(String error) {
}
