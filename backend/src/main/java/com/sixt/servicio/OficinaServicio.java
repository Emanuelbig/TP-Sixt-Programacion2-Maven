package com.sixt.servicio;

import com.sixt.dto.DatosOficina;
import com.sixt.excepcion.RecursoNoEncontradoExcepcion;
import com.sixt.modelo.Oficina;
import com.sixt.repositorio.OficinaRepositorio;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OficinaServicio {

    private final OficinaRepositorio oficinaRepositorio;

    public List<Oficina> listar() {
        return oficinaRepositorio.findAll();
    }

    public Oficina buscar(Long id) {
        return oficinaRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("la oficina", id));
    }

    public Oficina crear(DatosOficina datos) {
        return guardar(new Oficina(), datos);
    }

    @Transactional
    public Oficina actualizar(Long id, DatosOficina datos) {
        return guardar(buscar(id), datos);
    }

    private Oficina guardar(Oficina oficina, DatosOficina datos) {
        oficina.setZona(datos.zona().trim());
        oficina.setDireccion(datos.direccion().trim());
        return oficinaRepositorio.save(oficina);
    }
}
