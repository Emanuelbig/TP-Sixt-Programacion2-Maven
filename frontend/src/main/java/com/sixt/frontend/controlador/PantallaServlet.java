package com.sixt.frontend.controlador;

import com.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

// Esqueleto comun de todas las pantallas con tabla + formulario (patron Template Method):
//  - GET: carga los datos y muestra la vista.
//  - Despues de un POST: si el backend respondio bien, redirige (asi F5 no repite la accion);
//    si respondio con error, vuelve a mostrar la pantalla con el mensaje.
// Cada pantalla solo dice QUE datos carga, QUE vista usa y, si tiene ABM, sobre QUE recurso trabaja.
public abstract class PantallaServlet extends HttpServlet {

    protected abstract void cargarDatos(HttpServletRequest request) throws ServletException;

    protected abstract String vista();

    // Recurso de la pantalla ("OFICINA", "VEHICULO"...). Las pantallas con ABM lo redefinen;
    // con el, la vista sabe que formularios y botones mostrar segun los permisos del usuario.
    protected String recurso() {
        return null;
    }

    // Una pantalla que trabaja sobre varios recursos (Personas: empleados y clientes) redefine
    // este metodo: alcanza con tener la accion sobre alguno de ellos.
    protected String[] recursos() {
        if (recurso() == null) {
            return new String[0];
        }
        return new String[] {recurso()};
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cargarDatos(request);
        marcarPermisos(request);
        request.getRequestDispatcher("/WEB-INF/jsp/" + vista()).forward(request, response);
    }

    // Deja puedeCrear, puedeEditar y puedeEliminar para la vista. Es solo para no mostrar
    // lo que no se puede usar: la decision real la toma siempre el backend.
    private void marcarPermisos(HttpServletRequest request) {
        String[] recursos = recursos();
        if (recursos.length == 0) {
            return;
        }
        request.setAttribute("puedeCrear", puedeSobreAlguno(request, recursos, "CREAR"));
        request.setAttribute("puedeEditar", puedeSobreAlguno(request, recursos, "EDITAR"));
        request.setAttribute("puedeEliminar", puedeSobreAlguno(request, recursos, "ELIMINAR"));
    }

    private static boolean puedeSobreAlguno(HttpServletRequest request, String[] recursos, String accion) {
        for (String recurso : recursos) {
            if (UsuarioEnSesion.puede(request, recurso + ":" + accion)) {
                return true;
            }
        }
        return false;
    }

    protected void responder(ClienteBackend.Respuesta respuesta, String redireccion,
                             HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (respuesta.ok()) {
            response.sendRedirect(request.getContextPath() + redireccion);
        } else {
            request.setAttribute("error", respuesta.error());
            doGet(request, response);
        }
    }

    // Si la URL trae ?editar=5, deja en "editando" el elemento 5 para precargar el formulario
    protected void marcarEdicion(HttpServletRequest request, List<Map<String, Object>> lista) {
        String id = request.getParameter("editar");
        if (id == null) {
            id = request.getParameter("id");
        }
        for (Map<String, Object> elemento : lista) {
            if (String.valueOf(elemento.get("id")).equals(id)) {
                request.setAttribute("editando", elemento);
                return;
            }
        }
    }

    protected static boolean esEdicion(HttpServletRequest request) {
        String id = request.getParameter("id");
        return id != null && !id.isBlank();
    }
}
