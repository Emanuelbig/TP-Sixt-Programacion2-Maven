package com.sixt.servicio.reporte;

import com.sixt.modelo.DetalleReserva;
import com.sixt.modelo.Oficina;
import com.sixt.modelo.Persona;
import com.sixt.modelo.Reserva;
import com.sixt.modelo.Vehiculo;
import com.sixt.repositorio.OficinaRepositorio;
import com.sixt.repositorio.ReservaRepositorio;
import com.sixt.repositorio.VehiculoRepositorio;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Arma el reporte de un periodo recorriendo una sola vez las reservas. Solo lee.
// Reglas:
//  - las canceladas no se facturan (solo se cuentan aparte);
//  - una reserva se factura completa en el periodo en que empieza;
//  - para la ocupacion se cuentan los dias alquilados que caen dentro del periodo,
//    aunque la reserva haya empezado antes o termine despues.
@Service
@RequiredArgsConstructor
public class ReporteServicio {

    private static final String ONLINE = "online";

    private final ReservaRepositorio reservaRepositorio;
    private final VehiculoRepositorio vehiculoRepositorio;
    private final OficinaRepositorio oficinaRepositorio;

    @Transactional(readOnly = true)
    public ReporteReservas generar(Periodo periodo) {
        // Todos los vehiculos activos y todas las oficinas aparecen, aunque no tengan reservas
        Map<Long, AcumuladoVehiculo> porVehiculo = new LinkedHashMap<>();
        for (Vehiculo vehiculo : vehiculoRepositorio.findByFechaBajaIsNull()) {
            porVehiculo.put(vehiculo.getId(), new AcumuladoVehiculo(vehiculo, periodo.dias()));
        }
        Map<Long, AcumuladoOficina> porOficina = new LinkedHashMap<>();
        for (Oficina oficina : oficinaRepositorio.findAll()) {
            porOficina.put(oficina.getId(), new AcumuladoOficina(oficina));
        }
        Map<String, Acumulado> porVendedor = new LinkedHashMap<>();
        Map<YearMonth, AcumuladoMes> porMes = new LinkedHashMap<>();
        for (YearMonth mes : periodo.meses()) {
            porMes.put(mes, new AcumuladoMes(mes));
        }
        Acumulado total = new Acumulado("total", "Total");

        for (Reserva reserva : reservaRepositorio.noCanceladasQueSeSuperponenCon(periodo.getDesde(), periodo.getHasta())) {
            long diasEnElPeriodo = periodo.diasEnComun(reserva.getFechaDesde(), reserva.getFechaHasta());
            boolean empiezaEnElPeriodo = periodo.incluye(reserva.getFechaDesde());

            for (DetalleReserva detalle : reserva.getDetalles()) {
                AcumuladoVehiculo vehiculo = vehiculo(porVehiculo, detalle.getVehiculo(), periodo);
                vehiculo.sumarDias(diasEnElPeriodo);
                total.sumarDias(diasEnElPeriodo);
                if (empiezaEnElPeriodo) {
                    vehiculo.sumarReserva(detalle.getPrecioAlquiler());
                }
            }

            if (empiezaEnElPeriodo) {
                BigDecimal monto = reserva.getPrecioTotal();
                total.sumarReserva(monto);
                oficina(porOficina, reserva.getOficinaOrigen()).sumarReserva(monto);
                oficina(porOficina, reserva.getOficinaDestino()).sumarDevolucion();
                vendedor(porVendedor, reserva.getVendedor()).sumarReserva(monto);
                porMes.get(YearMonth.from(reserva.getFechaDesde())).sumarReserva(monto);
            }
        }

        long diasDisponibles = porVehiculo.size() * periodo.dias();
        int ocupacion = Porcentaje.de(total.getDiasAlquilado(), diasDisponibles);
        long canceladas = reservaRepositorio.countByFechaCancelacionIsNotNullAndFechaDesdeBetween(
                periodo.getDesde(), periodo.getHasta());

        return new ReporteReservas(periodo, total, ocupacion, canceladas,
                ordenados(porVehiculo), ordenados(porOficina), ordenados(porVendedor), conBarras(porMes));
    }

    // Un vehiculo dado de baja que tuvo reservas en el periodo tambien aparece
    private static AcumuladoVehiculo vehiculo(Map<Long, AcumuladoVehiculo> filas, Vehiculo vehiculo, Periodo periodo) {
        AcumuladoVehiculo fila = filas.get(vehiculo.getId());
        if (fila == null) {
            fila = new AcumuladoVehiculo(vehiculo, periodo.dias());
            filas.put(vehiculo.getId(), fila);
        }
        return fila;
    }

    private static AcumuladoOficina oficina(Map<Long, AcumuladoOficina> filas, Oficina oficina) {
        AcumuladoOficina fila = filas.get(oficina.getId());
        if (fila == null) {
            fila = new AcumuladoOficina(oficina);
            filas.put(oficina.getId(), fila);
        }
        return fila;
    }

    // Sin vendedor es una reserva online, hecha por el propio cliente
    private static Acumulado vendedor(Map<String, Acumulado> filas, Persona vendedor) {
        String clave = vendedor == null ? ONLINE : String.valueOf(vendedor.getId());
        Acumulado fila = filas.get(clave);
        if (fila == null) {
            String nombre = vendedor == null ? "Reservas online (sin vendedor)" : vendedor.getNombre();
            fila = new Acumulado(clave, nombre);
            filas.put(clave, fila);
        }
        return fila;
    }

    // De mayor a menor facturacion (ver Acumulado.compareTo)
    private static <T extends Acumulado> List<T> ordenados(Map<?, T> filas) {
        List<T> lista = new ArrayList<>(filas.values());
        Collections.sort(lista);
        return lista;
    }

    // Los meses quedan en orden de calendario; la barra mas larga es la del mes que mas facturo
    private static List<AcumuladoMes> conBarras(Map<YearMonth, AcumuladoMes> porMes) {
        BigDecimal maximo = BigDecimal.ZERO;
        for (AcumuladoMes mes : porMes.values()) {
            if (mes.getFacturado().compareTo(maximo) > 0) {
                maximo = mes.getFacturado();
            }
        }
        List<AcumuladoMes> meses = new ArrayList<>(porMes.values());
        for (AcumuladoMes mes : meses) {
            mes.ajustarBarra(maximo);
        }
        return meses;
    }
}
