package com.sixt.seguridad.servicio;

import com.sixt.seguridad.modelo.Usuario;
import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

// Emite el JWT del login. Lleva solo el id del usuario y la version de sus tokens:
// los permisos se leen de la base en cada pedido, asi un cambio del sysadmin
// se aplica en el proximo pedido sin esperar a que el token venza.
@Service
public class TokenServicio {

    public static final String CLAIM_VERSION = "version";

    private final JwtEncoder codificador;
    private final Duration duracion;

    public TokenServicio(JwtEncoder codificador, @Value("${sixt.jwt.duracion:8h}") Duration duracion) {
        this.codificador = codificador;
        this.duracion = duracion;
    }

    public String emitir(Usuario usuario) {
        Instant ahora = Instant.now();
        JwtClaimsSet datos = JwtClaimsSet.builder()
                .issuer("sixt")
                .issuedAt(ahora)
                .expiresAt(ahora.plus(duracion))
                .subject(usuario.getId().toString())
                .claim(CLAIM_VERSION, usuario.getVersionToken())
                .build();
        JwsHeader cabecera = JwsHeader.with(MacAlgorithm.HS256).build();
        return codificador.encode(JwtEncoderParameters.from(cabecera, datos)).getTokenValue();
    }
}
