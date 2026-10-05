package ar.edu.utn.inspt.sixt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "booking")
@Getter
@Setter
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String plate;
    private String colour;
    @Column(name = "vehicle_year")
    private int year;
    private Instant created_at;
    private int gas_liters;
    private int total_price;
    private Boolean is_returned;

    @Column(name = "date_from", nullable = false)
    private LocalDate date_from;

    @Column(name = "date_to", nullable = false)
    private LocalDate date_to;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Person client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salesman_id", nullable = false)
    private Person salesman;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_office_id", nullable = false)
    private Office origin_office;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_office_id", nullable = false)
    private Office destination_office;
}