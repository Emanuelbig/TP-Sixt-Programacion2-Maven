package com.sixt.frontend.controlador;

import com.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

// Pantalla de solo lectura: no tiene doPost, asi que no se puede mandar nada
@WebServlet("/reportes")
public class ReportesServlet extends PantallaServlet {

    @Override
    protected void cargarDatos(HttpServletRequest request) throws ServletException {
        request.setAttribute("filas", ClienteBackend.getLista(request, "/api/reportes/vehiculos"));
    }

    @Override
    protected String vista() {
        return "reportes.jsp";
    }
}