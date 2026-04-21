package com.example.transport.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateTripRequestDTO {

    private Long vehicleId;

    private LocalDateTime departureDateTime;

    private String departureLocation;
    private String destinationLocation;

    private Integer totalNoOfPassengers;

    private BigDecimal price;
}