package com.example.transport.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBookingDTO {

    @NotNull(message = "Trip ID is required")
    private Long tripId;

    @NotNull(message = "Number of seats is required")
    @Min(value = 1, message = "You must book at least 1 seat")
    private Integer numberOfSeats;
}