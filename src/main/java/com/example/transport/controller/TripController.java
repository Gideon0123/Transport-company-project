package com.example.transport.controller;

import com.example.transport.dto.*;
import com.example.transport.enums.TripStatus;
import com.example.transport.payload.ApiResponse;
import com.example.transport.payload.PagedResponse;
import com.example.transport.service.TripService;
import com.example.transport.util.TraceIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
@CrossOrigin
public class TripController {

    private final TripService tripService;

    //CREATE TRIP
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<TripResponseDTO>> createTrip(
            @Valid @RequestBody CreateTripRequestDTO dto,
            HttpServletRequest request) {
        TripResponseDTO trip = tripService.createTrip(dto);

        return ResponseEntity.ok(
                ApiResponse.<TripResponseDTO>builder()
                        .success(true)
                        .message("Trip Created successfully")
                        .statusCode(201)
                        .data(trip)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //GET ALL TRIPS
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<TripSummaryDTO>>> getPagedTrips(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "tripId") String sortBy,
            HttpServletRequest request
    ) {
        int adjustedPage = Math.max(page - 1, 0);
        PagedResponse<TripSummaryDTO> trips = tripService.getPagedTrips(adjustedPage, size, sortBy);
        PagedResponse<TripSummaryDTO> response = PagedResponse.<TripSummaryDTO>builder()
                .content(trips.getContent())
                .size(trips.getSize())
                .page(trips.getPage())
                .first(trips.isFirst())
                .last(trips.isLast())
                .totalElements(trips.getTotalElements())
                .totalPages(trips.getTotalPages())
                .build();

        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<TripSummaryDTO>>builder()
                        .success(true)
                        .message("Trips fetched successfully")
                        .statusCode(200)
                        .data(response)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //GET TRIP
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TripResponseDTO>> getTrip(
            @PathVariable Long id,
            HttpServletRequest request) {
        TripResponseDTO trip = tripService.getTrip(id);

        return ResponseEntity.ok(
                ApiResponse.<TripResponseDTO>builder()
                        .success(true)
                        .message("Trip fetched successfully")
                        .statusCode(200)
                        .data(trip)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //UPDATE TRIP
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TripResponseDTO>> updateTrip(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTripRequestDTO dto,
            HttpServletRequest request) {
        TripResponseDTO trip = tripService.updateTrip(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<TripResponseDTO>builder()
                        .success(true)
                        .message("Trip Updated successfully")
                        .statusCode(200)
                        .data(trip)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //DELETE TRIP
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteTrip(
            @PathVariable Long id,
            HttpServletRequest request
    ) {

        tripService.deleteTrip(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Trip deleted successfully")
                        .statusCode(200)
                        .data(null)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //SEARCH TRIP
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<TripResponseDTO>>> searchTrips(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long tripId,
            @RequestParam(required = false) String departureLocation,
            @RequestParam(required = false) String destinationLocation,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate bookingDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate departureDateTime,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) String tripStatus,

            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) String vehicleType,
            @RequestParam(required = false) String vehiclePlate,

            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "price") String sortBy,

            HttpServletRequest request
    ) {

        //Convert to Spring format (0-based)
        int adjustedPage = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by(sortBy));

        Page<TripResponseDTO> trips = tripService.searchTrips(
                keyword,
                tripId,
                departureLocation,
                destinationLocation,
                bookingDate,
                departureDateTime,
                price,
                tripStatus,

                vehicleId,
                vehicleType,
                vehiclePlate,
                pageable
        );

        //Convert back to 1-based
        PagedResponse<TripResponseDTO> response = PagedResponse.<TripResponseDTO>builder()
                .content(trips.getContent())
                .page(trips.getNumber() + 1)
                .size(trips.getSize())
                .totalElements(trips.getTotalElements())
                .totalPages(trips.getTotalPages())
                .first(trips.isFirst())
                .last(trips.isLast())
                .build();

        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<TripResponseDTO>>builder()
                        .success(true)
                        .message("Trips fetched successfully")
                        .statusCode(200)
                        .data(response)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //PATCH TRIP
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TripResponseDTO>> updateTripStatus(
            @PathVariable Long id,
            @RequestParam TripStatus status,
            HttpServletRequest request
    ) {

        TripResponseDTO updatedTrip = tripService.updateTripStatus(id, status);

        return ResponseEntity.ok(
                ApiResponse.<TripResponseDTO>builder()
                        .success(true)
                        .message("Trip status updated successfully")
                        .statusCode(200)
                        .data(updatedTrip)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}