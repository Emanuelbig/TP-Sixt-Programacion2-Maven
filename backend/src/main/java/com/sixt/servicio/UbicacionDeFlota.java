package com.sixt.servicio;

import com.sixt.excepcion.ConflictoExcepcion;
import com.sixt.modelo.Oficina;
import com.sixt.modelo.Reserva;
import com.sixt.modelo.Vehiculo;
import com.sixt.repositorio.ReservaRepositorio;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// Donde va a estar cada vehiculo y si puede tomar una reserva nueva.
// Un auto que se reserva de Oficina 1 a Oficina 2 hasta el 15/11 queda en Oficina 2:
// desde el 16/11 se puede retirar ahi, y no en Oficina 1.
// Lo usan el buscador de disponibles y el alta de reservas, asi la regla vive en un solo lugar.
@Service
@RequiredArgsConstructor
public class UbicacionDeFlota {

    private final ReservaRepositorio reservaRepositorio;

    // La oficina de destino de la ultima reserva pendiente que termina antes de esa fecha.
    // Sin reservas pendientes, la oficina donde esta hoy (al devolver una reserva se actualiza).
    public Oficina oficinaEn(Vehiculo vehiculo, LocalDate fecha) {
        List<Reserva> anteriores = reservaRepositorio.pendientesQueTerminanAntesDe(vehiculo.getId(), fecha);
        if (anteriores.isEmpty()) {
            return vehiculo.getOficina();
        }
        return anteriores.get(0).getOficinaDestino();
    }

    public boolean estaDisponible(Vehiculo vehiculo, LocalDate desde, LocalDate hasta,
                                  Long oficinaRetiroId, Long oficinaDevolucionId) {
        return motivoDeRechazo(vehiculo, desde, hasta, oficinaRetiroId, oficinaDevolucionId) == null;
    }

    public void exigirDisponible(Vehiculo vehiculo, LocalDate desde, LocalDate hasta,
                                 Long oficinaRetiroId, Long oficinaDevolucionId) {
        String motivo = motivoDeRechazo(vehiculo, desde, hasta, oficinaRetiroId, oficinaDevolucionId);
        if (motivo != null) {
            throw new ConflictoExcepcion("El vehiculo " + vehiculo.getPatente() + " " + motivo);
        }
    }

    // null si se puede reservar. Tres condiciones:
    //  1. no tiene otra reserva que se pise con esas fechas;
    //  2. al empezar, va a estar en la oficina de retiro;
    //  3. si tiene una reserva despues, se devuelve en la oficina de donde sale esa reserva
    //     (si no, la reserva siguiente se quedaria sin auto).
    private String motivoDeRechazo(Vehiculo vehiculo, LocalDate desde, LocalDate hasta,
                                   Long oficinaRetiroId, Long oficinaDevolucionId) {
        if (reservaRepositorio.vehiculoOcupadoEntre(vehiculo.getId(), desde, hasta)) {
            return "no esta disponible entre el " + desde + " y el " + hasta;
        }
        Oficina alEmpezar = oficinaEn(vehiculo, desde);
        if (!alEmpezar.getId().equals(oficinaRetiroId)) {
            return "va a estar en " + alEmpezar.getZona() + " el " + desde + ", no en la oficina de retiro";
        }
        List<Reserva> siguientes = reservaRepositorio.pendientesQueEmpiezanDespuesDe(vehiculo.getId(), hasta);
        if (!siguientes.isEmpty()) {
            Oficina proximoRetiro = siguientes.get(0).getOficinaOrigen();
            if (!proximoRetiro.getId().equals(oficinaDevolucionId)) {
                return "tiene que devolverse en " + proximoRetiro.getZona()
                        + " para una reserva del " + siguientes.get(0).getFechaDesde();
            }
        }
        return null;
    }
}
