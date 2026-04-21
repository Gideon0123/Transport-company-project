package com.example.transport.service;

import com.example.transport.dto.*;
import com.example.transport.enums.TripStatus;
import com.example.transport.payload.PagedResponse;

import java.time.LocalDate;
import java.util.List;

public interface TripService {
    TripResponseDTO createTrip(CreateTripRequestDTO dto);
//    Page<TripSummaryDTO> getPagedTrips(int page, int size, String sortBy);
    PagedResponse<TripSummaryDTO> getPagedTrips(int page, int size, String sortBy);
    TripResponseDTO getTrip(Long id);
    TripResponseDTO updateTrip(Long id, UpdateTripRequestDTO dto);
    TripResponseDTO updateTripStatus(Long id, TripStatus newStatus);
    void deleteTrip(Long id);
    List<TripResponseDTO> searchTrips(String vehicleType, String departureLocation, String destinationLocation, LocalDate bookingDate, LocalDate departureDateTime);
}
