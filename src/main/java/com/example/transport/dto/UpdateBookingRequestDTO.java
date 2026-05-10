package com.example.transport.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateBookingRequestDTO {

    @NotNull(message = "Select the number of seats you want to book")
    @Min(value = 1, message = "You must book at least 1 seat")
    private Integer numberOfSeats;
}