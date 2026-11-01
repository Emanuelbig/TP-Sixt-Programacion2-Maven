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
// Cada pantalla solo dice QUE datos carga y QUE vista usa.
public abstract class PantallaServlet extends HttpServlet {

    protected abstract void cargarDatos(HttpServletRequest request) throws ServletException;

    protected abstract String vista();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cargarDatos(request);
        request.getRequestDispatcher("/WEB-INF/jsp/" + vista()).forward(request, response);
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
