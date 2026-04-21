package com.example.transport.service;

import com.example.transport.dto.*;
import com.example.transport.payload.PagedResponse;

import java.util.List;

public interface VehicleService {
    VehicleResponseDTO createVehicle(CreateVehicleRequestDTO dto);
//    Page<VehicleSummaryDTO> getPagedVehicles(int page, int size, String sortBy);
    PagedResponse<VehicleSummaryDTO> getPagedVehicles(int page, int size, String sortBy);
    VehicleResponseDTO getVehicle(Long id);
    VehicleResponseDTO updateVehicle(Long id, UpdateVehicleRequestDTO dto);
    void deleteVehicle(Long id);
    List<VehicleResponseDTO> searchVehicles (String driver, String vehiclePlate, String vehicleType);

}
