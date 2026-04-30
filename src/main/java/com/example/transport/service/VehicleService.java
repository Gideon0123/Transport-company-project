package com.example.transport.service;

import com.example.transport.dto.*;
import com.example.transport.payload.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VehicleService {
    VehicleResponseDTO createVehicle(CreateVehicleRequestDTO dto);
    PagedResponse<VehicleSummaryDTO> getPagedVehicles(int page, int size, String sortBy);
    VehicleResponseDTO getVehicle(Long id);
    VehicleResponseDTO updateVehicle(Long id, UpdateVehicleRequestDTO dto);
    void deleteVehicle(Long id);
    Page<VehicleResponseDTO> searchVehicles (
            String keyword,
            Long driverId,
            String vehiclePlate,
            String vehicleType,
            String vehicleStatus,
            Pageable pageable);
}
