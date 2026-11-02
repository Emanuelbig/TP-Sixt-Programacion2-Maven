package com.sixt.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sixt.seguridad.modelo.Accion;
import com.sixt.seguridad.modelo.Recurso;
import com.sixt.seguridad.modelo.Rol;
import com.sixt.seguridad.modelo.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Datos de negocio de una persona. Los de acceso (email, contraseña, rol) son de la
// app seguridad, en Usuario. Persona comparte el id con su Usuario (@MapsId): el id
// del token es tambien el de la persona.
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "persona")
@Getter
@Setter
@NoArgsConstructor
public class Persona {

    @Id
    private Long id;

    @JsonIgnore
    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id")
    private Usuario usuario;

    @Column(unique = true, nullable = false)
    private int dni;

    private String nombre;

    @Column(length = 500)
    private String direccion;

    private String telefono;

    private Instant fechaCreacion;
    private Instant fechaBaja;

    // El JSON sigue mostrando email y rol, como antes de separar Usuario
    @JsonProperty
    public String getEmail() {
        return usuario.getEmail();
    }

    @JsonProperty
    public Rol getRol() {
        return usuario.getRol();
    }

    // Baja logica: la persona sigue en la base (por sus reservas) pero ya no opera
    public boolean estaActiva() {
        return fechaBaja == null;
    }

    public boolean puede(Recurso recurso, Accion accion) {
        return getRol().tiene(recurso, accion);
    }

    @PrePersist
    void alCrear() {
        fechaCreacion = Instant.now();
    }
}
