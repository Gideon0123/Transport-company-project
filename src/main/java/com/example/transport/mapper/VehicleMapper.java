package com.example.transport.mapper;

import com.example.transport.dto.DriverDTO;
import com.example.transport.dto.VehicleResponseDTO;
import com.example.transport.model.Staff;
import com.example.transport.model.Vehicle;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleMapper {

    public static VehicleResponseDTO toDTO(Vehicle vehicle) {

        if (vehicle == null) return null;

        VehicleResponseDTO dto = new VehicleResponseDTO();

        dto.setVehicleId(vehicle.getVehicleId());
        dto.setVehiclePlate(vehicle.getVehiclePlate());
        dto.setVehicleType(vehicle.getVehicleType().name());
        dto.setVehicleStatus(vehicle.getStatus());

        if (vehicle.getDriver() != null) {

            Staff driver = vehicle.getDriver();

            DriverDTO driverDTO = new DriverDTO();
            driverDTO.setStaffId(driver.getStaffId());

            if (driver.getUser() != null) {
                driverDTO.setFirstName(driver.getUser().getFirstName());
                driverDTO.setLastName(driver.getUser().getLastName());
            }

            dto.setDriver(driverDTO);
        }

        return dto;
    }
}