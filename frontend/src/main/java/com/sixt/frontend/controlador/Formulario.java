package com.sixt.frontend.controlador;

import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Arma los datos a mandar al backend a partir de los campos de un formulario.
// Un campo vacio o que no vino se manda como null, y es el backend el que
// decide si es valido (asi la validacion vive en un solo lugar).
final class Formulario {

    private Formulario() {
    }

    static Map<String, Object> campos(HttpServletRequest request, String... nombres) {
        Map<String, Object> datos = new LinkedHashMap<>();
        for (String nombre : nombres) {
            String valor = request.getParameter(nombre);
            datos.put(nombre, valor == null || valor.isBlank() ? null : valor.trim());
        }
        return datos;
    }

    // Para campos que pueden venir varias veces, como un grupo de checkboxes
    static List<String> lista(HttpServletRequest request, String nombre) {
        String[] valores = request.getParameterValues(nombre);
        return valores == null ? List.of() : List.of(valores);
    }
}
