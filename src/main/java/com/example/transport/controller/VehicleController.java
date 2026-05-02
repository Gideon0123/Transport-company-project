package com.example.transport.controller;

import com.example.transport.dto.*;
import com.example.transport.payload.ApiResponse;
import com.example.transport.payload.PagedResponse;
import com.example.transport.service.VehicleService;
import com.example.transport.util.TraceIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/test-admin")
    public String test() {
        return "OK";
    }

    //CREATE
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<VehicleResponseDTO>> createVehicle(
            @RequestBody CreateVehicleRequestDTO dto,
            HttpServletRequest request) {

        VehicleResponseDTO vehicle = vehicleService.createVehicle(dto);

        return ResponseEntity.ok(
                ApiResponse.<VehicleResponseDTO>builder()
                        .success(true)
                        .message("Vehicle Created successfully")
                        .statusCode(201)
                        .data(vehicle)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //PAGINATION
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<VehicleSummaryDTO>>> getPagedVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "vehicleId") String sortBy,
            HttpServletRequest request
    ) {
        PagedResponse<VehicleSummaryDTO> vehicles = vehicleService.getPagedVehicles(page, size, sortBy);
        PagedResponse<VehicleSummaryDTO> response = PagedResponse.<VehicleSummaryDTO>builder()
                .content(vehicles.getContent())
                .size(vehicles.getSize())
                .totalElements(vehicles.getTotalElements())
                .totalPages(vehicles.getTotalPages())
                .build();

        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<VehicleSummaryDTO>>builder()
                        .success(true)
                        .message("Vehicles fetched successfully")
                        .statusCode(200)
                        .data(response)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //GET BY ID
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponseDTO>> getVehicle(
            @PathVariable Long id,
            HttpServletRequest request) {
        VehicleResponseDTO vehicle = vehicleService.getVehicle(id);

        return ResponseEntity.ok(
                ApiResponse.<VehicleResponseDTO>builder()
                        .success(true)
                        .message("Vehicle Fetched successfully")
                        .statusCode(200)
                        .data(vehicle)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //UPDATE
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponseDTO>> updateVehicle(
            @PathVariable Long id,
            @RequestBody UpdateVehicleRequestDTO dto,
            HttpServletRequest request) {

        VehicleResponseDTO vehicle = vehicleService.updateVehicle(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<VehicleResponseDTO>builder()
                        .success(true)
                        .message("Vehicle Updated successfully")
                        .statusCode(200)
                        .data(vehicle)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //DELETE
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteVehicle(@PathVariable Long id, HttpServletRequest request) {
        vehicleService.deleteVehicle(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Vehicle deleted successfully")
                        .statusCode(200)
                        .data(null)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //SEARCH
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<VehicleResponseDTO>>> searchVehicles(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) String vehiclePlate,
            @RequestParam(required = false) String vehicleType,
            @RequestParam(required = false) String vehicleStatus,

            @PageableDefault(size = 5, sort = "vehiclePlate")
            Pageable pageable,
            HttpServletRequest request
    ) {
        Page<VehicleResponseDTO> vehicles = vehicleService.searchVehicles(
                keyword,
                driverId,
                vehiclePlate,
                vehicleType,
                vehicleStatus,
                pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<VehicleResponseDTO>>builder()
                        .success(true)
                        .message("Vehicles fetched successfully")
                        .statusCode(200)
                        .data(vehicles)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
