package ar.edu.utn.inspt.sixt.dao;

import java.util.List;

/**
 *
 * @author Ian
 */

//incluir un parametro adicional para leer el ID del objeto
public interface DAO<T> {

    void guardar(List<T> entidades);

    List<T> leer();

}
