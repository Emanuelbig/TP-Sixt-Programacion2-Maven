package com.sixt.frontend;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import org.apache.jasper.JspC;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

// Compila todos los JSP con Jasper, el mismo compilador que usa Tomcat 10.1.
// Un error en un JSP (una etiqueta mal cerrada, una expresion EL invalida, un include
// que no existe) normalmente aparece recien al abrir la pantalla en el navegador;
// con este test aparece al correr "mvn verify".
class JspCompilacionTest {

    private static final File WEBAPP = new File("src/main/webapp");

    @Test
    void todosLosJspCompilan(@TempDir Path salida) throws Exception {
        JspC compilador = new JspC();
        compilador.setUriroot(WEBAPP.getAbsolutePath());
        // Solo las paginas .jsp: los .jspf son fragmentos que se compilan dentro de la pagina que los incluye
        compilador.setJspFiles(paginas(new File(WEBAPP, "WEB-INF/jsp")));
        compilador.setOutputDir(salida.toString());
        compilador.setCompile(true);
        compilador.setFailOnError(true);
        compilador.setJavaEncoding("UTF-8");
        compilador.setClassPath(System.getProperty("java.class.path"));
        compilador.execute();

        // Que haya generado clases: si no encontro ningun JSP, el test no probaria nada
        assertTrue(contarClases(salida.toFile()) >= 10, "Jasper no compilo los JSP esperados");
    }

    // "WEB-INF/jsp/inicio.jsp,WEB-INF/jsp/login.jsp,...", como lo pide JspC
    private static String paginas(File carpeta) {
        StringBuilder lista = new StringBuilder();
        File[] archivos = carpeta.listFiles();
        if (archivos == null) {
            return "";
        }
        for (File archivo : archivos) {
            if (archivo.getName().endsWith(".jsp")) {
                if (lista.length() > 0) {
                    lista.append(',');
                }
                lista.append("WEB-INF/jsp/").append(archivo.getName());
            }
        }
        return lista.toString();
    }

    private static int contarClases(File carpeta) {
        int cantidad = 0;
        File[] archivos = carpeta.listFiles();
        if (archivos == null) {
            return 0;
        }
        for (File archivo : archivos) {
            if (archivo.isDirectory()) {
                cantidad += contarClases(archivo);
            } else if (archivo.getName().endsWith("_jsp.class")) {
                cantidad++;
            }
        }
        return cantidad;
    }
}
