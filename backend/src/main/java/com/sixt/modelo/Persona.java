package com.sixt.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "persona")
@Getter
@Setter
@NoArgsConstructor
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private int dni;

    private String nombre;

    @Column(unique = true, nullable = false)
    private String email;

    private String telefono;

    // Hash BCrypt, nunca la contraseña en texto plano. No sale en el JSON.
    @JsonIgnore
    @Column(nullable = false)
    private String contrasenia;

    private Instant fechaCreacion;
    private Instant fechaBaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    // Baja logica: la persona sigue en la base (por sus reservas) pero ya no opera
    public boolean estaActiva() {
        return fechaBaja == null;
    }

    public boolean puede(Permiso permiso) {
        return rol.tiene(permiso);
    }

    @PrePersist
    void alCrear() {
        fechaCreacion = Instant.now();
    }
}
