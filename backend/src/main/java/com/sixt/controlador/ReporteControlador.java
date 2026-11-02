package com.sixt.controlador;

import com.sixt.servicio.reporte.Periodo;
import com.sixt.servicio.reporte.ReporteReservas;
import com.sixt.servicio.reporte.ReporteServicio;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Solo tiene GET: es de solo lectura (la URL exige REPORTE:VER)
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteControlador {

    private final ReporteServicio reporteServicio;

    // GET /api/reportes?desde=2026-01-01&hasta=2026-12-31
    // Sin fechas, el año en curso
    @GetMapping
    public ReporteReservas reporte(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        Periodo periodo;
        if (desde == null && hasta == null) {
            periodo = Periodo.anioDe(LocalDate.now());
        } else {
            periodo = new Periodo(desde, hasta);
        }
        return reporteServicio.generar(periodo);
    }
}
