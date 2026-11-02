package com.sixt.servicio;

import com.sixt.dto.DatosVehiculo;
import com.sixt.excepcion.ConflictoExcepcion;
import com.sixt.excepcion.DatosInvalidosExcepcion;
import com.sixt.excepcion.RecursoNoEncontradoExcepcion;
import com.sixt.modelo.Vehiculo;
import com.sixt.repositorio.ModeloRepositorio;
import com.sixt.repositorio.ReservaRepositorio;
import com.sixt.repositorio.VehiculoRepositorio;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VehiculoServicio {

    private final VehiculoRepositorio vehiculoRepositorio;
    private final ModeloRepositorio modeloRepositorio;
    private final ReservaRepositorio reservaRepositorio;
    private final OficinaServicio oficinaServicio;
    private final UbicacionDeFlota ubicacionDeFlota;

    // Solo los que no fueron dados de baja
    public List<Vehiculo> listar() {
        return vehiculoRepositorio.findByFechaBajaIsNull();
    }

    // Recorre los activos y se queda con los que no estan reservados en ese rango
    public List<Vehiculo> listarDisponibles(LocalDate desde, LocalDate hasta,
                                            Long oficinaRetiroId, Long oficinaDevolucionId) {
        if (hasta.isBefore(desde)) {
            throw new DatosInvalidosExcepcion("La fecha de fin no puede ser anterior a la de inicio");
        }
        List<Vehiculo> disponibles = new ArrayList<>();
        for (Vehiculo vehiculo : listar()) {
            if (ubicacionDeFlota.estaDisponible(vehiculo, desde, hasta, oficinaRetiroId, oficinaDevolucionId)) {
                disponibles.add(vehiculo);
            }
        }
        return disponibles;
    }

    public Vehiculo buscar(Long id) {
        return vehiculoRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("el vehiculo", id));
    }

    // Para operar (reservar, editar) el vehiculo tiene que seguir activo
    public Vehiculo buscarActivo(Long id) {
        Vehiculo vehiculo = buscar(id);
        if (!vehiculo.estaActivo()) {
            throw new DatosInvalidosExcepcion("El vehiculo " + vehiculo.getPatente() + " fue dado de baja");
        }
        return vehiculo;
    }

    @Transactional
    public Vehiculo crear(DatosVehiculo datos) {
        String patente = datos.patente().toUpperCase();
        validarAnio(datos.anio());
        if (vehiculoRepositorio.existsByPatenteIgnoreCase(patente)) {
            throw new ConflictoExcepcion("Ya existe un vehiculo con la patente " + patente);
        }
        return guardar(new Vehiculo(), datos);
    }

    @Transactional
    public Vehiculo actualizar(Long id, DatosVehiculo datos) {
        Vehiculo vehiculo = buscarActivo(id);
        String patente = datos.patente().toUpperCase();
        validarAnio(datos.anio());
        if (vehiculoRepositorio.existsByPatenteIgnoreCaseAndIdNot(patente, id)) {
            throw new ConflictoExcepcion("Ya existe otro vehiculo con la patente " + patente);
        }
        return guardar(vehiculo, datos);
    }

    // Baja logica: se marca la fecha y deja de aparecer, pero sus reservas viejas siguen intactas
    @Transactional
    public void darDeBaja(Long id) {
        Vehiculo vehiculo = buscarActivo(id);
        if (reservaRepositorio.existsByDetallesVehiculoIdAndDevueltoFalseAndFechaCancelacionIsNull(id)) {
            throw new ConflictoExcepcion("El vehiculo " + vehiculo.getPatente()
                    + " tiene reservas sin devolver, no se puede dar de baja");
        }
        vehiculo.setFechaBaja(Instant.now());
    }

    private Vehiculo guardar(Vehiculo vehiculo, DatosVehiculo datos) {
        vehiculo.setPatente(datos.patente().toUpperCase());
        vehiculo.setColor(datos.color().trim());
        vehiculo.setAnio(datos.anio());
        vehiculo.setModelo(modeloRepositorio.findById(datos.modeloId())
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("el modelo", datos.modeloId())));
        vehiculo.setOficina(oficinaServicio.buscarActiva(datos.oficinaId()));
        return vehiculoRepositorio.save(vehiculo);
    }

    // Se aceptan modelos del año que viene, que ya se venden en el actual
    private void validarAnio(int anio) {
        int anioMaximo = Year.now().getValue() + 1;
        if (anio > anioMaximo) {
            throw new DatosInvalidosExcepcion("El año no puede ser posterior a " + anioMaximo);
        }
    }
}
