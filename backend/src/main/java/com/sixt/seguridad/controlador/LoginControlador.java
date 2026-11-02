package com.sixt.seguridad.controlador;

import com.sixt.seguridad.dto.DatosLogin;
import com.sixt.seguridad.dto.RespuestaLogin;
import com.sixt.seguridad.servicio.AutenticacionServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Unico endpoint publico de la API: devuelve el token que va en cada pedido siguiente
// como "Authorization: Bearer <token>"
@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginControlador {

    private final AutenticacionServicio autenticacionServicio;

    // Si las credenciales no son validas, el servicio corta con un 401
    @PostMapping
    public RespuestaLogin login(@Valid @RequestBody DatosLogin datos) {
        return autenticacionServicio.login(datos);
    }
}
