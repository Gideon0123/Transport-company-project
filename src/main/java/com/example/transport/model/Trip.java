package com.example.transport.model;

import com.example.transport.enums.TripStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trip extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tripId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = true)
    private Vehicle vehicle;

    private BigDecimal price;

    private LocalDate bookingDate = LocalDate.now();

    private LocalDateTime departureDateTime;

    private String departureLocation;

    private String destinationLocation;

    private Integer totalNoOfPassengers;

    private String vehiclePlateSnapshot;

    private String vehicleTypeSnapshot;

    @Column(nullable = false)
    private boolean deleted = false;

    @Enumerated(EnumType.STRING)
    private TripStatus status = TripStatus.PENDING;

    @OneToMany(mappedBy = "trip", fetch = FetchType.LAZY)
    private List<CustomerTrip> customerTrips;


}
