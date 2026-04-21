package com.example.transport.mapper;

import com.example.transport.dto.TripResponseDTO;
import com.example.transport.dto.VehicleSimpleDTO;
import com.example.transport.model.Trip;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TripMapper {

    public static TripResponseDTO toDTO(Trip trip) {
        if (trip == null) return null;

        TripResponseDTO dto = new TripResponseDTO();

        dto.setTripId(trip.getTripId());
        dto.setPrice(trip.getPrice());
        dto.setBookingDate(trip.getBookingDate());
        dto.setDepartureDateTime(trip.getDepartureDateTime());
        dto.setDepartureLocation(trip.getDepartureLocation());
        dto.setDestinationLocation(trip.getDestinationLocation());
        dto.setTotalNoOfPassengers(trip.getTotalNoOfPassengers());
        dto.setTripStatus(trip.getStatus());

        // Map vehicle WITHOUT trips
        if (trip.getVehicle() != null) {
            VehicleSimpleDTO vehicleDTO = new VehicleSimpleDTO();
            vehicleDTO.setVehicleId(trip.getVehicle().getVehicleId());
            vehicleDTO.setVehiclePlate(trip.getVehicle().getVehiclePlate());
            vehicleDTO.setVehicleType(trip.getVehicle().getVehicleType().name());

            dto.setVehicle(vehicleDTO);
        }

        return dto;
    }
}