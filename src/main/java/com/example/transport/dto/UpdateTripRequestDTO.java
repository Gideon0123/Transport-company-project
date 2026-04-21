package com.example.transport.dto;

import com.example.transport.enums.TripStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UpdateTripRequestDTO {
    private BigDecimal price;
    private LocalDate bookingDate;
    private LocalDateTime departureDateTime;
    private String departureLocation;
    private String destinationLocation;
    private Integer totalNoOfPassengers;
    private Long vehicleId;
    private TripStatus tripStatus;
}