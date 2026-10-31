package ar.edu.utn.inspt.sixt.controlador;

import ar.edu.utn.inspt.sixt.servicio.ResumenServicio;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resumen")
@RequiredArgsConstructor
public class ResumenControlador {

    private final ResumenServicio resumenServicio;

    @GetMapping
    public Map<String, Long> resumen() {
        return resumenServicio.obtener();
    }
}
