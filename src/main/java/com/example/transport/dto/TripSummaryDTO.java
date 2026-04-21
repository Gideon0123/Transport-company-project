package com.example.transport.dto;

import com.example.transport.enums.TripStatus;
import com.example.transport.enums.VehicleType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
public class TripSummaryDTO {

    private Long tripId;
    private String departureLocation;
    private String destinationLocation;
    private BigDecimal price;
    private String vehiclePlate;
    private VehicleType vehicleType;
//    private TripStatus status;

    public TripSummaryDTO(Long tripId,
                          String departureLocation,
                          String destinationLocation,
                          BigDecimal price,
                          String vehiclePlate,
                          VehicleType vehicleType
//                          TripStatus status
    ) {
        this.tripId = tripId;
        this.departureLocation = departureLocation;
        this.destinationLocation = destinationLocation;
        this.price = price;
        this.vehiclePlate = vehiclePlate;
        this.vehicleType = vehicleType;
//        this.status = status;
    }
}