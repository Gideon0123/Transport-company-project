package com.example.transport.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequestDTO {

    @NotNull(message = "Please select a Trip")
    private Long tripId;

    @NotNull(message = "Select the number of seats you want to book")
    @Min(value = 1, message = "You must book at least 1 seat")
    private Integer numberOfSeats;
}
