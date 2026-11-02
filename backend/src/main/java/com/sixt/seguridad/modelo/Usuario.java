package com.sixt.seguridad.modelo;

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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Datos de acceso de una cuenta (como User en Django). Los datos de negocio
// (DNI, nombre, direccion...) viven en Persona, que comparte el id con su Usuario.
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    // Hash BCrypt, nunca la contraseña en texto plano. No sale en el JSON.
    @JsonIgnore
    @Column(nullable = false)
    private String contrasenia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private boolean activo = true;

    // Va dentro de cada token. Al cerrar sesion o al darse de baja se incrementa,
    // y todos los tokens emitidos antes dejan de valer aunque no hayan vencido.
    @JsonIgnore
    @Column(nullable = false)
    private int versionToken;

    public void invalidarTokens() {
        versionToken++;
    }

    public void desactivar() {
        activo = false;
        invalidarTokens();
    }

    // El usuario decide si un token emitido para el todavia vale
    public boolean aceptaToken(int version) {
        return activo && versionToken == version;
    }
}
