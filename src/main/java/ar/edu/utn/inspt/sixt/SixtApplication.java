package ar.edu.utn.inspt.sixt;

//para los acentos
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import java.util.Scanner;
import ar.edu.utn.inspt.sixt.menu.MenuPrincipal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ar.edu.utn.inspt.sixt.servicios.SixtServicio;

/**
 * 
 *
 * 
 * antes JVM llamaba directo a nuestro main y listo. Ahora JVM llama a
 * Spring, Spring levanta su contenedor y recien cuando termina de
 * inicializarse ejecuta el metodo run() de todos los CommandLineRunner que
 * encuentre. Ahi es donde enganchamos nuestro menu de consola.
 * 
 * 
 *
 * Grupo H - Programacion 2 - Turno Noche
 */
@SpringBootApplication
//CommandLineRunner es una interface con el metodo run(), con esto arrancamos spring
public class SixtApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SixtApplication.class, args);
    }

    
    @Override
    public void run(String... args) {
        // Esto lo pongo para que podamos usar acentos en la consola
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        Scanner scanner = new Scanner(System.in);
        SixtServicio servicio = new SixtServicio();
        MenuPrincipal menuPrincipal = new MenuPrincipal(scanner, servicio);
        menuPrincipal.mostrar();
        scanner.close();
    }
}
