package ar.edu.utn.inspt.sixt.servicio;

import ar.edu.utn.inspt.sixt.repositorio.OficinaRepositorio;
import ar.edu.utn.inspt.sixt.repositorio.PersonaRepositorio;
import ar.edu.utn.inspt.sixt.repositorio.ReservaRepositorio;
import ar.edu.utn.inspt.sixt.repositorio.VehiculoRepositorio;
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
                "oficinas", oficinaRepositorio.count(),
                "vehiculos", vehiculoRepositorio.count(),
                "personas", personaRepositorio.count(),
                "reservas", reservaRepositorio.count());
    }
}
