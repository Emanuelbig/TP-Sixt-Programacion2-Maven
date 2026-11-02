package com.sixt.seguridad.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Los roles son datos: el sysadmin los crea, edita y borra desde la pantalla.
// En la base se guarda solo lo que eligio; la cascada se calcula al vuelo,
// asi si una accion pasa a incluir otra, todos los roles la reciben sin migrar datos.
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "rol")
@Getter
@Setter
@NoArgsConstructor
public class Rol {

    private static final Set<Permiso> PERMISOS_DE_CLIENTE = Set.of(
            Permiso.de(Recurso.RESERVA, Accion.VER_PROPIAS),
            Permiso.de(Recurso.RESERVA, Accion.ALQUILAR));

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "rol_permiso", joinColumns = @JoinColumn(name = "rol_id"))
    private Set<Permiso> permisos = new HashSet<>();

    public Rol(String nombre, Set<Permiso> permisos) {
        this.nombre = nombre;
        this.permisos = new HashSet<>(permisos);
    }

    // Lo elegido mas todo lo que incluye por la cascada
    public Set<Permiso> getPermisosEfectivos() {
        Set<Permiso> efectivos = new HashSet<>();
        for (Permiso permiso : permisos) {
            efectivos.addAll(permiso.efectivos());
        }
        return efectivos;
    }

    // Los efectivos como texto ordenado ("OFICINA:VER", ...), para el JSON y las authorities
    public Set<String> codigosDePermisosEfectivos() {
        Set<String> codigos = new TreeSet<>();
        for (Permiso permiso : getPermisosEfectivos()) {
            codigos.add(permiso.toString());
        }
        return codigos;
    }

    public boolean tiene(Permiso permiso) {
        return getPermisosEfectivos().contains(permiso);
    }

    public boolean tiene(Recurso recurso, Accion accion) {
        return tiene(Permiso.de(recurso, accion));
    }

    // Un rol es "de cliente" si solo puede ver sus reservas y alquilar.
    // Esas personas se gestionan con CLIENTE:*, el resto con USUARIO:*
    public boolean esDeCliente() {
        return getPermisosEfectivos().equals(PERMISOS_DE_CLIENTE);
    }

    public void reemplazarPermisos(Set<Permiso> nuevos) {
        permisos.clear();
        permisos.addAll(nuevos);
    }
}
