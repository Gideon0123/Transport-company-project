package com.example.transport.dto;

import com.example.transport.enums.TripStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTripStatusRequestDTO {

    @NotNull(message = "Status is required")
    private TripStatus tripStatus;
}
