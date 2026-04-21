package com.example.transport.dto;

import com.example.transport.enums.VehicleStatus;
import com.example.transport.enums.VehicleType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VehicleSummaryDTO {
    private Long vehicleId;
    private String vehiclePlate;
    private VehicleType vehicleType;
    private VehicleStatus status;

    public VehicleSummaryDTO (Long vehicleId,
                              String vehiclePlate,
                              VehicleType vehicleType,
                              VehicleStatus status) {
        this.vehicleId = vehicleId;
        this.vehiclePlate = vehiclePlate;
        this.vehicleType = vehicleType;
        this.status = status;
    }
}