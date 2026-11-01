package com.sixt.controlador;

import com.sixt.dto.UsuarioLogueado;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SesionControlador {
    @GetMapping("/sesion")
    public UsuarioLogueado sesion(HttpServletRequest request) {
        return (UsuarioLogueado) request.getAttribute("usuarioAutenticado");
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) { request.getSession(false).invalidate(); }
}
