package com.example.transport.dto;

import com.example.transport.enums.VehicleStatus;
import lombok.Data;

@Data
public class UpdateVehicleRequestDTO {

    private String vehiclePlate;
    private String vehicleType;
    private VehicleStatus vehicleStatus;
    private Long driverId;

}
