package ar.edu.utn.inspt.sixt.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ConfiguracionSeguridad {

    // Los servicios dependen de la interfaz PasswordEncoder: si un dia se cambia
    // el algoritmo, se cambia solo esta linea.
    @Bean
    public PasswordEncoder codificadorContrasenias() {
        return new BCryptPasswordEncoder();
    }
}
