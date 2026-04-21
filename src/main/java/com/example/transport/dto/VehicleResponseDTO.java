package com.example.transport.dto;

import com.example.transport.enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponseDTO {

    private Long vehicleId;
    private String vehiclePlate;
    private String vehicleType;
    private VehicleStatus vehicleStatus;

    private DriverDTO driver;

    // Optional: include trips WITHOUT vehicle inside
    // private List<TripSimpleDTO> trips;
}
