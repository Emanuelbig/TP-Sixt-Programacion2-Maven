package com.sixt.servicio;

import com.sixt.dto.DatosModelo;
import com.sixt.dto.DatosTipoModelo;
import com.sixt.excepcion.ConflictoExcepcion;
import com.sixt.excepcion.RecursoNoEncontradoExcepcion;
import com.sixt.modelo.Modelo;
import com.sixt.modelo.TipoModelo;
import com.sixt.repositorio.ModeloRepositorio;
import com.sixt.repositorio.TipoModeloRepositorio;
import com.sixt.repositorio.VehiculoRepositorio;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Catalogo de la flota: los tipos (con su precio por dia) y los modelos de cada tipo.
// Cambiar un precio solo afecta a las reservas nuevas: cada reserva guarda el precio
// con el que se hizo (DetalleReserva.precioAlquiler).
@Service
@RequiredArgsConstructor
public class ModeloServicio {

    private final ModeloRepositorio modeloRepositorio;
    private final TipoModeloRepositorio tipoModeloRepositorio;
    private final VehiculoRepositorio vehiculoRepositorio;

    // ----- Tipos y precios -----

    public List<TipoModelo> listarTipos() {
        return tipoModeloRepositorio.findAll();
    }

    public TipoModelo buscarTipo(Long id) {
        TipoModelo tipo = tipoModeloRepositorio.findById(id).orElse(null);
        if (tipo == null) {
            throw new RecursoNoEncontradoExcepcion("el tipo de modelo", id);
        }
        return tipo;
    }

    @Transactional
    public TipoModelo crearTipo(DatosTipoModelo datos) {
        String nombre = datos.nombre().trim().toUpperCase();
        if (tipoModeloRepositorio.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictoExcepcion("Ya existe el tipo " + nombre);
        }
        return guardarTipo(new TipoModelo(), nombre, datos);
    }

    @Transactional
    public TipoModelo actualizarTipo(Long id, DatosTipoModelo datos) {
        TipoModelo tipo = buscarTipo(id);
        String nombre = datos.nombre().trim().toUpperCase();
        if (tipoModeloRepositorio.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ConflictoExcepcion("Ya existe otro tipo llamado " + nombre);
        }
        return guardarTipo(tipo, nombre, datos);
    }

    // Los tipos se guardan en mayusculas (AUTO, CAMIONETA), igual que los que carga el sistema
    // Se borra de verdad: solo se permite si ningun modelo es de este tipo
    @Transactional
    public void eliminarTipo(Long id) {
        TipoModelo tipo = buscarTipo(id);
        if (modeloRepositorio.existsByTipoId(id)) {
            throw new ConflictoExcepcion("Hay modelos del tipo " + tipo.getNombre() + ", no se puede eliminar");
        }
        tipoModeloRepositorio.delete(tipo);
    }

    private TipoModelo guardarTipo(TipoModelo tipo, String nombre, DatosTipoModelo datos) {
        tipo.setNombre(nombre);
        tipo.setPrecio(datos.precio());
        return tipoModeloRepositorio.save(tipo);
    }

    // ----- Modelos -----

    public List<Modelo> listarModelos() {
        return modeloRepositorio.findAll();
    }

    public Modelo buscarModelo(Long id) {
        Modelo modelo = modeloRepositorio.findById(id).orElse(null);
        if (modelo == null) {
            throw new RecursoNoEncontradoExcepcion("el modelo", id);
        }
        return modelo;
    }

    @Transactional
    public Modelo crearModelo(DatosModelo datos) {
        String marca = datos.marca().trim();
        String nombre = datos.nombre().trim();
        if (modeloRepositorio.existsByMarcaIgnoreCaseAndNombreIgnoreCase(marca, nombre)) {
            throw new ConflictoExcepcion("Ya existe el modelo " + marca + " " + nombre);
        }
        return guardarModelo(new Modelo(), marca, nombre, datos.tipoId());
    }

    @Transactional
    public Modelo actualizarModelo(Long id, DatosModelo datos) {
        Modelo modelo = buscarModelo(id);
        String marca = datos.marca().trim();
        String nombre = datos.nombre().trim();
        if (modeloRepositorio.existsByMarcaIgnoreCaseAndNombreIgnoreCaseAndIdNot(marca, nombre, id)) {
            throw new ConflictoExcepcion("Ya existe otro modelo " + marca + " " + nombre);
        }
        return guardarModelo(modelo, marca, nombre, datos.tipoId());
    }

    // Se borra de verdad: solo se permite si ningun vehiculo (ni siquiera uno dado de baja) lo usa
    @Transactional
    public void eliminarModelo(Long id) {
        Modelo modelo = buscarModelo(id);
        if (vehiculoRepositorio.existsByModeloId(id)) {
            throw new ConflictoExcepcion("Hay vehiculos " + modelo.getMarca() + " " + modelo.getNombre()
                    + ", no se puede eliminar el modelo");
        }
        modeloRepositorio.delete(modelo);
    }

    private Modelo guardarModelo(Modelo modelo, String marca, String nombre, Long tipoId) {
        modelo.setMarca(marca);
        modelo.setNombre(nombre);
        modelo.setTipo(buscarTipo(tipoId));
        return modeloRepositorio.save(modelo);
    }
}
