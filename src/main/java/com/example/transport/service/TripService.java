package com.example.transport.service;

import com.example.transport.dto.*;
import com.example.transport.enums.TripStatus;
import com.example.transport.payload.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TripService {
    TripResponseDTO createTrip(CreateTripRequestDTO dto);
    PagedResponse<TripSummaryDTO> getPagedTrips(int page, int size, String sortBy);
    TripResponseDTO getTrip(Long id);
    TripResponseDTO updateTrip(Long id, UpdateTripRequestDTO dto);
    TripResponseDTO updateTripStatus(Long id, TripStatus newStatus);
    void deleteTrip(Long id);
    Page<TripResponseDTO> searchTrips(
            String keyword,
            Long tripId,
            String departureLocation,
            String destinationLocation,
            LocalDate bookingDate,
            LocalDate departureDateTime,
            BigDecimal price,
            String tripStatus,

            Long vehicleId,
            String vehicleType,
            String vehiclePlate,
            Pageable pageable
    );
}
