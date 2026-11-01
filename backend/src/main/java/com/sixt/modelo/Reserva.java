package com.sixt.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "reserva")
@Getter
@Setter
@NoArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant fechaCreacion;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    @Setter(lombok.AccessLevel.NONE)
    private List<DetalleReserva> detalles = new ArrayList<>();

    public void agregarDetalle(DetalleReserva detalle) {
        detalle.setReserva(this);
        detalles.add(detalle);
    }

    @Column(precision = 12, scale = 2)
    private BigDecimal precioTotal;

    private Boolean devuelto;

    // Null = vigente. Si tiene fecha, se cancelo y el auto queda libre para esas fechas
    private Instant fechaCancelacion;

    @Column(nullable = false)
    private LocalDate fechaDesde;

    @Column(nullable = false)
    private LocalDate fechaHasta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Persona cliente;

    // Null cuando el cliente reservo solo desde la web (reserva online, sin vendedor)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id")
    private Persona vendedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oficina_origen_id", nullable = false)
    private Oficina oficinaOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oficina_destino_id", nullable = false)
    private Oficina oficinaDestino;

    @PrePersist
    void alCrear() {
        fechaCreacion = Instant.now();
    }
}
