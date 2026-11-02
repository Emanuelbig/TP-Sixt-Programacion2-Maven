package com.sixt.seguridad.dto;

import com.sixt.seguridad.modelo.Accion;
import com.sixt.seguridad.modelo.Recurso;
import java.util.ArrayList;
import java.util.List;

// Una fila de la matriz de permisos: el recurso y las acciones que admite.
// "incluye" dice que otras acciones quedan cubiertas por la cascada.
public record RecursoInfo(String recurso, String descripcion, List<AccionInfo> acciones) {

    public record AccionInfo(String accion, String codigo, String descripcion, List<String> incluye) {
    }

    public static RecursoInfo de(Recurso recurso) {
        List<AccionInfo> acciones = new ArrayList<>();
        for (Accion accion : recurso.getAcciones()) {
            List<String> incluye = new ArrayList<>();
            for (Accion incluida : accion.incluidas()) {
                if (incluida != accion && recurso.admite(incluida)) {
                    incluye.add(incluida.name());
                }
            }
            acciones.add(new AccionInfo(accion.name(), recurso + ":" + accion, accion.getDescripcion(), incluye));
        }
        return new RecursoInfo(recurso.name(), recurso.getDescripcion(), acciones);
    }
}
