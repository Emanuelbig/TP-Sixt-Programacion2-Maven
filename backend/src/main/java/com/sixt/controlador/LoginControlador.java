package com.sixt.controlador;

import com.sixt.dto.DatosLogin;
import com.sixt.dto.UsuarioLogueado;
import com.sixt.servicio.AutenticacionServicio;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginControlador {

    private final AutenticacionServicio autenticacionServicio;

    // Si las credenciales no son validas, el servicio corta con un 401
    @PostMapping
    public UsuarioLogueado login(@Valid @RequestBody DatosLogin datos, HttpServletRequest request) {
        UsuarioLogueado usuario = autenticacionServicio.login(datos);
        var anterior = request.getSession(false);
        if (anterior != null) anterior.invalidate();
        var sesion = request.getSession(true);
        sesion.setAttribute("usuarioId", usuario.id());
        sesion.setMaxInactiveInterval(30 * 60);
        return usuario;
    }
}
