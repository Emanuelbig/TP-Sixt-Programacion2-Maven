package com.sixt.servicio;

import com.sixt.repositorio.OficinaRepositorio;
import com.sixt.repositorio.PersonaRepositorio;
import com.sixt.repositorio.ReservaRepositorio;
import com.sixt.repositorio.VehiculoRepositorio;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// Contadores para la pantalla de inicio
@Service
@RequiredArgsConstructor
public class ResumenServicio {

    private final OficinaRepositorio oficinaRepositorio;
    private final VehiculoRepositorio vehiculoRepositorio;
    private final PersonaRepositorio personaRepositorio;
    private final ReservaRepositorio reservaRepositorio;

    public Map<String, Long> obtener() {
        return Map.of(
                // Solo lo activo: lo dado de baja no aparece en las pantallas, tampoco en el resumen
                "oficinas", oficinaRepositorio.countByFechaBajaIsNull(),
                "vehiculos", vehiculoRepositorio.countByFechaBajaIsNull(),
                "personas", personaRepositorio.countByFechaBajaIsNull(),
                "reservas", reservaRepositorio.count());
    }
}
