package com.sixt.frontend.controlador;

import com.sixt.frontend.ClienteBackend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

// Pantalla de solo lectura (no tiene doPost): indicadores de un periodo y cuatro pestañas.
// Todo viaja en la URL (?desde=...&hasta=...&vista=...), asi un reporte se puede guardar o compartir.
@WebServlet("/reportes")
public class ReportesServlet extends PantallaServlet {

    private static final List<String> VISTAS = List.of("vehiculos", "oficinas", "vendedores", "meses");

    @Override
    protected void cargarDatos(HttpServletRequest request) throws ServletException {
        request.setAttribute("vista", vistaElegida(request));
        cargarAtajos(request);

        String ruta = "/api/reportes";
        String desde = request.getParameter("desde");
        String hasta = request.getParameter("hasta");
        if (periodoValido(request, desde, hasta)) {
            ruta = ruta + "?desde=" + desde + "&hasta=" + hasta;
        }
        // Sin periodo (o con uno invalido) el backend devuelve el año en curso
        request.setAttribute("reporte", ClienteBackend.getObjeto(request, ruta));
    }

    @Override
    protected String vista() {
        return "reportes.jsp";
    }

    private static String vistaElegida(HttpServletRequest request) {
        String vista = request.getParameter("vista");
        if (vista == null || !VISTAS.contains(vista)) {
            return VISTAS.get(0);
        }
        return vista;
    }

    private static boolean periodoValido(HttpServletRequest request, String desde, String hasta) {
        if (desde == null || desde.isBlank() || hasta == null || hasta.isBlank()) {
            return false;
        }
        try {
            if (LocalDate.parse(hasta).isBefore(LocalDate.parse(desde))) {
                request.setAttribute("error", "La fecha \"Hasta\" no puede ser anterior a \"Desde\": se muestra el año en curso");
                return false;
            }
        } catch (DateTimeParseException e) {
            request.setAttribute("error", "Las fechas no tienen un formato válido: se muestra el año en curso");
            return false;
        }
        return true;
    }

    // Periodos de un clic: este mes, este año y el año pasado
    private static void cargarAtajos(HttpServletRequest request) {
        LocalDate hoy = LocalDate.now();
        LocalDate anioPasado = hoy.minusYears(1);
        request.setAttribute("mesDesde", hoy.withDayOfMonth(1).toString());
        request.setAttribute("mesHasta", hoy.withDayOfMonth(hoy.lengthOfMonth()).toString());
        request.setAttribute("anioDesde", hoy.withDayOfYear(1).toString());
        request.setAttribute("anioHasta", hoy.withDayOfYear(hoy.lengthOfYear()).toString());
        request.setAttribute("anioPasadoDesde", anioPasado.withDayOfYear(1).toString());
        request.setAttribute("anioPasadoHasta", anioPasado.withDayOfYear(anioPasado.lengthOfYear()).toString());
    }
}
