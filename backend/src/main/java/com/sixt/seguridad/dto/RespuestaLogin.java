package com.sixt.seguridad.dto;

import java.util.Set;

// Lo que devuelve el login: el token para los proximos pedidos y quien entro (nunca la contraseña)
public record RespuestaLogin(String token, Long id, String email, String rol, Set<String> permisos) {
}
