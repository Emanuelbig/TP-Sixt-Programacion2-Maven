package com.sixt.servicio;

import com.sixt.dto.FilaReporte;
import com.sixt.modelo.DetalleReserva;
import com.sixt.modelo.Reserva;
import com.sixt.repositorio.ReservaRepositorio;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Arma los reportes. Solo lee: no crea, no modifica, no borra
@Service
@RequiredArgsConstructor
public class ReporteServicio {

    private final ReservaRepositorio reservaRepositorio;

    // readOnly: la transaccion solo lee, y deja cargar los detalles de cada reserva
    @Transactional(readOnly = true)
    public List<FilaReporte> porVehiculo() {
        List<FilaReporte> filas = new ArrayList<>();
        for (Reserva reserva : reservaRepositorio.findAll()) {
            // Las canceladas no se cobraron: no cuentan
            if (reserva.getFechaCancelacion() == null) {
                for (DetalleReserva detalle : reserva.getDetalles()) {
                    FilaReporte fila = buscarFila(filas, detalle.getPatente());
                    if (fila == null) {
                        fila = new FilaReporte(detalle.getPatente());
                        filas.add(fila);
                    }
                    fila.sumar(detalle.getPrecioAlquiler());
                }
            }
        }
        return filas;
    }

    // Devuelve la fila de esa patente, o null si todavia no esta
    private FilaReporte buscarFila(List<FilaReporte> filas, String patente) {
        for (FilaReporte fila : filas) {
            if (fila.getPatente().equals(patente)) {
                return fila;
            }
        }
        return null;
    }
}