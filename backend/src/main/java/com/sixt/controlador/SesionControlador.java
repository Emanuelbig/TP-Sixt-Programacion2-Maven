package com.sixt.controlador;

import com.sixt.dto.UsuarioLogueado;
import com.sixt.seguridad.servicio.UsuarioActual;
import com.sixt.servicio.PersonaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Datos de quien hizo el pedido y sus permisos vigentes. El frontend lo consulta en cada
// pantalla, asi los menus reflejan enseguida un cambio de rol hecho por el sysadmin.
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SesionControlador {

    private final PersonaServicio personaServicio;
    private final UsuarioActual usuarioActual;

    @GetMapping("/sesion")
    @Transactional(readOnly = true)
    public UsuarioLogueado sesion() {
        return UsuarioLogueado.de(personaServicio.buscar(usuarioActual.id()));
    }
}
