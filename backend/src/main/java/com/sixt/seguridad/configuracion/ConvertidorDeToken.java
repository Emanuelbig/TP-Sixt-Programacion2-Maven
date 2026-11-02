package com.sixt.seguridad.configuracion;

import com.sixt.excepcion.CredencialesInvalidasExcepcion;
import com.sixt.seguridad.servicio.TokenServicio;
import com.sixt.seguridad.servicio.UsuarioServicio;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

// Spring ya valido la firma y el vencimiento del token. Aca se busca al usuario por el id
// del token y se cargan sus permisos efectivos ACTUALES como authorities.
// Si fue dado de baja o cerro sesion, el pedido termina en 401.
@RequiredArgsConstructor
class ConvertidorDeToken implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UsuarioServicio usuarioServicio;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Long id;
        int version;
        try {
            id = Long.valueOf(jwt.getSubject());
            version = ((Number) jwt.getClaim(TokenServicio.CLAIM_VERSION)).intValue();
        } catch (RuntimeException e) {
            throw new InvalidBearerTokenException("El token no tiene el formato esperado");
        }

        Set<String> permisos;
        try {
            permisos = usuarioServicio.permisosVigentes(id, version);
        } catch (CredencialesInvalidasExcepcion e) {
            throw new InvalidBearerTokenException("La sesion ya no es valida");
        }

        List<GrantedAuthority> autoridades = new ArrayList<>();
        for (String permiso : permisos) {
            autoridades.add(new SimpleGrantedAuthority(permiso));
        }
        return new JwtAuthenticationToken(jwt, autoridades, jwt.getSubject());
    }
}
