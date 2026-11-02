package com.sixt.controlador;

import com.sixt.dto.FilaReporte;
import com.sixt.servicio.ReporteServicio;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Solo tiene GET: es de solo lectura
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteControlador {

    private final ReporteServicio reporteServicio;

    // GET /api/reportes/vehiculos
    @GetMapping("/vehiculos")
    public List<FilaReporte> porVehiculo() {
        return reporteServicio.porVehiculo();
    }
}