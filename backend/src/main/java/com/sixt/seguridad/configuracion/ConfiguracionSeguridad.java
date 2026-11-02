package com.sixt.seguridad.configuracion;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.sixt.seguridad.servicio.UsuarioServicio;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Arma la cadena de filtros de Spring Security: la API valida cada pedido con un JWT.
// Que pide cada URL lo decide ReglasDeAcceso; las reglas que dependen del dato concreto
// viven en los servicios o en un @PreAuthorize.
// Spring Security 7 se configura con objetos Customizer: aca son clases anonimas.
@Configuration
@EnableMethodSecurity
public class ConfiguracionSeguridad {

    // Los servicios dependen de la interfaz PasswordEncoder (DIP): si un dia se cambia
    // el algoritmo, se cambia solo esta linea.
    @Bean
    public PasswordEncoder codificadorContrasenias() {
        return new BCryptPasswordEncoder();
    }

    // HS256 necesita una clave de al menos 256 bits (32 bytes)
    @Bean
    public SecretKey claveJwt(@Value("${sixt.jwt.secreto}") String secreto) {
        byte[] bytes = secreto.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("sixt.jwt.secreto tiene que tener al menos 32 caracteres");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder codificadorJwt(SecretKey claveJwt) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(claveJwt));
    }

    @Bean
    public JwtDecoder decodificadorJwt(SecretKey claveJwt) {
        return NimbusJwtDecoder.withSecretKey(claveJwt).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    public SecurityFilterChain cadenaDeFiltros(HttpSecurity http, UsuarioServicio usuarioServicio) throws Exception {
        final RespuestasDeSeguridad respuestas = new RespuestasDeSeguridad();
        final ConvertidorDeToken convertidor = new ConvertidorDeToken(usuarioServicio);

        // El token viaja en el header Authorization y no en una cookie: no hace falta CSRF
        http.csrf(new Customizer<CsrfConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CsrfConfigurer<HttpSecurity> csrf) {
                csrf.disable();
            }
        });

        // Sin sesion en el servidor: cada pedido trae su token
        http.sessionManagement(new Customizer<SessionManagementConfigurer<HttpSecurity>>() {
            @Override
            public void customize(SessionManagementConfigurer<HttpSecurity> sesion) {
                sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            }
        });

        http.authorizeHttpRequests(new ReglasDeAcceso());

        // Valida el JWT y lo convierte en un usuario con sus permisos vigentes
        http.oauth2ResourceServer(new Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>>() {
            @Override
            public void customize(OAuth2ResourceServerConfigurer<HttpSecurity> servidor) {
                servidor.jwt(new Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>.JwtConfigurer>() {
                    @Override
                    public void customize(OAuth2ResourceServerConfigurer<HttpSecurity>.JwtConfigurer jwt) {
                        jwt.jwtAuthenticationConverter(convertidor);
                    }
                });
                servidor.authenticationEntryPoint(respuestas);
                servidor.accessDeniedHandler(respuestas);
            }
        });

        // 401 y 403 con el mismo JSON que el resto de la API
        http.exceptionHandling(new Customizer<ExceptionHandlingConfigurer<HttpSecurity>>() {
            @Override
            public void customize(ExceptionHandlingConfigurer<HttpSecurity> errores) {
                errores.authenticationEntryPoint(respuestas);
                errores.accessDeniedHandler(respuestas);
            }
        });

        return http.build();
    }
}
