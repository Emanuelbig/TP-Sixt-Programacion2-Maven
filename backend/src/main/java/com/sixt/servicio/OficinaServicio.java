package com.sixt.servicio;

import com.sixt.dto.DatosOficina;
import com.sixt.excepcion.ConflictoExcepcion;
import com.sixt.excepcion.DatosInvalidosExcepcion;
import com.sixt.excepcion.RecursoNoEncontradoExcepcion;
import com.sixt.modelo.Oficina;
import com.sixt.repositorio.OficinaRepositorio;
import com.sixt.repositorio.ReservaRepositorio;
import com.sixt.repositorio.VehiculoRepositorio;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OficinaServicio {

    private final OficinaRepositorio oficinaRepositorio;
    private final VehiculoRepositorio vehiculoRepositorio;
    private final ReservaRepositorio reservaRepositorio;

    // Solo las que no fueron dadas de baja
    public List<Oficina> listar() {
        return oficinaRepositorio.findByFechaBajaIsNull();
    }

    public Oficina buscar(Long id) {
        return oficinaRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("la oficina", id));
    }

    // Para operar (reservar, ubicar un vehiculo, editar) la oficina tiene que seguir activa
    public Oficina buscarActiva(Long id) {
        Oficina oficina = buscar(id);
        if (!oficina.estaActiva()) {
            throw new DatosInvalidosExcepcion("La oficina " + oficina.getZona() + " fue dada de baja");
        }
        return oficina;
    }

    // Baja logica: deja de aparecer, pero las reservas viejas la siguen mostrando
    @Transactional
    public void darDeBaja(Long id) {
        Oficina oficina = buscarActiva(id);
        if (vehiculoRepositorio.existsByOficinaIdAndFechaBajaIsNull(id)) {
            throw new ConflictoExcepcion("La oficina " + oficina.getZona()
                    + " todavia tiene vehiculos: primero hay que moverlos o darlos de baja");
        }
        if (reservaRepositorio.tieneReservasPendientesEnOficina(id)) {
            throw new ConflictoExcepcion("La oficina " + oficina.getZona()
                    + " tiene reservas pendientes que salen o llegan ahi");
        }
        oficina.setFechaBaja(Instant.now());
    }

    public Oficina crear(DatosOficina datos) {
        return guardar(new Oficina(), datos);
    }

    @Transactional
    public Oficina actualizar(Long id, DatosOficina datos) {
        return guardar(buscarActiva(id), datos);
    }

    private Oficina guardar(Oficina oficina, DatosOficina datos) {
        oficina.setZona(datos.zona().trim());
        oficina.setDireccion(datos.direccion().trim());
        return oficinaRepositorio.save(oficina);
    }
}
