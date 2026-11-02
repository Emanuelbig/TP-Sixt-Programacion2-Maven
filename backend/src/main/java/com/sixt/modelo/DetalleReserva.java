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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
//tabla donde se guarda
@Table(name = "detalle_reserva", uniqueConstraints =
        @UniqueConstraint(name = "uk_detalle_reserva_vehiculo", columnNames = {"reserva_id", "vehiculo_id"}))
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
public class DetalleReserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    // Datos historicos del vehiculo y del alquiler, independientes de cambios posteriores.
    private String patente;
    private String color;
    private int anio;

    // Columna no nulo
    @Column(nullable = false)
    private int litrosNafta;

    // Precision 12 digitos en total, 2 digitos despues de la coma
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precioAlquiler;
}
