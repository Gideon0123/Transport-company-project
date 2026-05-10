package com.example.transport.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateVehicleRequestDTO {

    @NotNull(message = "Vehicle Without Plate Cannot Be Registered")
    private String vehiclePlate;

    @NotNull(message = "Please select Vehicle Type")
    private String vehicleType;
    private Long driverId;
}