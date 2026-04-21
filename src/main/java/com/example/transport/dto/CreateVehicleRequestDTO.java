package com.example.transport.dto;

import lombok.Data;

@Data
public class CreateVehicleRequestDTO {

    private String vehiclePlate;
    private String vehicleType;
    private Long driverId;
}