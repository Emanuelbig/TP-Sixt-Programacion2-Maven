package com.sixt.seguridad.controlador;

import com.sixt.seguridad.dto.CambioContrasenia;
import com.sixt.seguridad.servicio.UsuarioActual;
import com.sixt.seguridad.servicio.UsuarioServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Lo que cada uno hace sobre su propia cuenta. El id sale siempre del token,
// asi nadie puede cambiar la contraseña de otro ni cerrarle la sesion.
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CuentaControlador {

    private final UsuarioServicio usuarioServicio;
    private final UsuarioActual usuarioActual;

    @PatchMapping("/cuenta/contrasenia")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cambiarContrasenia(@Valid @RequestBody CambioContrasenia cambio) {
        usuarioServicio.cambiarContrasenia(usuarioActual.id(), cambio);
    }

    // Invalida todos los tokens emitidos hasta ahora para este usuario
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout() {
        usuarioServicio.cerrarSesion(usuarioActual.id());
    }
}
