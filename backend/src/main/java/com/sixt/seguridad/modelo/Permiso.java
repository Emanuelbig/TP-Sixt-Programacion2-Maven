package com.sixt.seguridad.modelo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

// Un permiso es un par RECURSO:ACCION, por ejemplo VEHICULO:EDITAR.
// Ese mismo texto es la authority de Spring Security y lo que viaja en el JSON.
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Permiso {

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "recurso", length = 32, nullable = false)
    private Recurso recurso;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "accion", length = 32, nullable = false)
    private Accion accion;

    public Permiso(Recurso recurso, Accion accion) {
        this.recurso = recurso;
        this.accion = accion;
    }

    public static Permiso de(Recurso recurso, Accion accion) {
        return new Permiso(recurso, accion);
    }

    // "VEHICULO:EDITAR" -> Permiso. Si el texto no es valido lanza IllegalArgumentException
    @JsonCreator
    public static Permiso de(String codigo) {
        if (codigo == null) {
            throw new IllegalArgumentException("Permiso invalido: null");
        }
        String[] partes = codigo.trim().toUpperCase().split(":");
        if (partes.length != 2) {
            throw new IllegalArgumentException("Permiso invalido: " + codigo);
        }
        return new Permiso(Recurso.valueOf(partes[0]), Accion.valueOf(partes[1]));
    }

    public boolean esValido() {
        return recurso.admite(accion);
    }

    public boolean esCapacidad() {
        return accion.esCapacidad();
    }

    // Este permiso mas los que incluye por la cascada, solo con acciones que el recurso admite
    public Set<Permiso> efectivos() {
        Set<Permiso> efectivos = new HashSet<>();
        for (Accion incluida : accion.incluidas()) {
            if (recurso.admite(incluida)) {
                efectivos.add(new Permiso(recurso, incluida));
            }
        }
        return efectivos;
    }

    @JsonValue
    @Override
    public String toString() {
        return recurso + ":" + accion;
    }
}
