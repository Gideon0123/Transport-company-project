package com.example.transport.service;

import com.example.transport.dto.*;
import com.example.transport.enums.TripStatus;
import com.example.transport.enums.VehicleType;
import com.example.transport.exception.InvalidStateException;
import com.example.transport.exception.ResourceNotFoundException;
import com.example.transport.mapper.TripMapper;
import com.example.transport.model.Staff;
import com.example.transport.model.Trip;
import com.example.transport.model.Vehicle;
import com.example.transport.payload.PagedResponse;
import com.example.transport.repository.TripRepository;
import com.example.transport.repository.VehicleRepository;
import com.example.transport.repository.specification.GenericSearchSpecification;
import com.example.transport.repository.specification.TripSearchSpecs;
import com.example.transport.util.CacheKeys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class TripServiceImpl implements TripService{

    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    @CacheEvict(value = CacheKeys.TRIP, allEntries = true)
    public TripResponseDTO createTrip(CreateTripRequestDTO dto) {

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        Staff driver = vehicle.getDriver();

        boolean driverBusy = tripRepository
                .existsByVehicle_DriverAndDepartureDateTimeBetween(
                        driver,
                        dto.getDepartureDateTime().minusHours(1),
                        dto.getDepartureDateTime().plusHours(5)
                );

        if (driverBusy) {
            throw new RuntimeException("Driver is already assigned to another trip");
        }

        Trip trip = Trip.builder()
                .vehicle(vehicle)
                .vehiclePlateSnapshot(vehicle.getVehiclePlate())
                .vehicleTypeSnapshot(vehicle.getVehicleType().name())
                .price(dto.getPrice())
                .departureDateTime(dto.getDepartureDateTime())
                .departureLocation(dto.getDepartureLocation())
                .destinationLocation(dto.getDestinationLocation())
                .totalNoOfPassengers(dto.getTotalNoOfPassengers())
                .status(TripStatus.PENDING)
                .build();

        return TripMapper.toDTO(tripRepository.save(trip));
    }

    @Override
    @Cacheable(value = CacheKeys.TRIP, key = "#page + '-' + #size + '-' + #sortBy")
    public PagedResponse<TripSummaryDTO> getPagedTrips(int page, int size, String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<TripSummaryDTO> tripPage = tripRepository.findAllTripsOptimized(pageable);

        return new PagedResponse<>(tripPage);
    }

    @Override
    @Cacheable(value = CacheKeys.TRIP, key = "#id")
    public TripResponseDTO getTrip(Long id) {

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        return TripMapper.toDTO(trip);
    }

    @Override
    @CacheEvict(value = CacheKeys.TRIP, allEntries = true)
    public TripResponseDTO updateTrip(Long id, UpdateTripRequestDTO dto) {

        Trip existingTrip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        if (existingTrip.getStatus() == TripStatus.ONGOING || existingTrip.getStatus() == TripStatus.COMPLETED) {
            throw new InvalidStateException("Cannot edit Trip once started or completed!!");
        }

        if (dto.getPrice() != null) {
            existingTrip.setPrice(dto.getPrice());
        }
        if (dto.getBookingDate() != null) {
            existingTrip.setBookingDate(dto.getBookingDate());
        }
        if (dto.getDepartureDateTime() != null) {
            existingTrip.setDepartureDateTime(dto.getDepartureDateTime());
        }
        if (dto.getDepartureLocation() != null) {
            existingTrip.setDepartureLocation(dto.getDepartureLocation());
        }
        if (dto.getDestinationLocation() != null) {
            existingTrip.setDestinationLocation(dto.getDestinationLocation());
        }
        if (dto.getTotalNoOfPassengers() != null) {
            existingTrip.setTotalNoOfPassengers(dto.getTotalNoOfPassengers());
        }

        if (dto.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
            existingTrip.setVehicle(vehicle);
        }
        if (dto.getTripStatus() != null) {
            existingTrip.setStatus(dto.getTripStatus());
        }

        return TripMapper.toDTO(tripRepository.save(existingTrip));
    }

    @Override
    @CacheEvict(value = CacheKeys.TRIP, allEntries = true)
    public TripResponseDTO updateTripStatus(Long id, TripStatus newStatus) {

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        TripStatus current = trip.getStatus();

        if (current == TripStatus.PENDING && newStatus == TripStatus.ONGOING) {
            trip.setStatus(newStatus);
        }
        else if (current == TripStatus.ONGOING && newStatus == TripStatus.COMPLETED) {
            trip.setStatus(newStatus);
        }
        else if (current == TripStatus.PENDING && newStatus == TripStatus.CANCELLED) {
            trip.setStatus(newStatus);
        }
        else if (current == null && newStatus == TripStatus.PENDING) {
            trip.setStatus(newStatus);
        }
        else {
            throw new InvalidStateException("Invalid status transition");
        }

        return TripMapper.toDTO(tripRepository.save(trip));
    }

    @Override
    @CacheEvict(value = CacheKeys.TRIP, allEntries = true)
    public void deleteTrip(Long id) {
        Trip trip = tripRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        trip.setDeleted(true);
        trip.setStatus(TripStatus.CANCELLED);
        tripRepository.deleteById(id);
    }

    @Override
    public Page<TripResponseDTO> searchTrips(
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
    ) {

        Map<String, Object> filters = new HashMap<>();

        if (tripId != null) {
            filters.put("tripId", tripId);
        }

        if (departureLocation != null && !departureLocation.isEmpty()) {
            filters.put("departureLocation", departureLocation);
        }

        if (destinationLocation != null && !destinationLocation.isEmpty()) {
            filters.put("destinationLocation", destinationLocation);
        }

        if (bookingDate != null) {
            filters.put("bookingDate", bookingDate);
        }

        if (departureDateTime != null) {
            filters.put("departureDateTime", departureDateTime);
        }


        if (price != null) {
            filters.put("price", price);
        }

        if (tripStatus != null && !tripStatus.isBlank()) {
            try {
                filters.put("status", TripStatus.valueOf(tripStatus.toUpperCase().trim()));
            } catch (IllegalArgumentException ignored) {}
        }
        // Vehicles search params

        if (vehicleId != null) {
            filters.put("vehicle.id", vehicleId);
        }

        if (vehicleType != null && !vehicleType.isBlank()) {
            try {
                filters.put("vehicle.vehicleType", VehicleType.valueOf(vehicleType.toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }

        if (vehiclePlate != null && !vehiclePlate.isEmpty()) {
            filters.put("vehicle.vehiclePlate", vehiclePlate);
        }

        Specification<Trip> spec =
                new GenericSearchSpecification<Trip>().build(filters);
        
        if (departureDateTime != null) {
            spec = (spec == null)
                    ? departureDateSpec(departureDateTime)
                    : spec.and(departureDateSpec(departureDateTime));
        }

        if (keyword != null && keyword.length() >= 3) {
            Specification<Trip> keywordSpec =
                    TripSearchSpecs.keywordSearch(keyword);

            spec = (spec == null)
                    ? keywordSpec
                    : spec.and(keywordSpec);
        }

        Page<Trip> tripPage =
                tripRepository.findAll(spec, pageable);

        return tripPage.map(TripMapper::toDTO);
    }

    private Specification<Trip> departureDateSpec(LocalDate date) {
        return (root, query, cb) -> cb.and(
                cb.greaterThanOrEqualTo(
                        root.get("departureDateTime"),
                        date.atStartOfDay()
                ),
                cb.lessThan(
                        root.get("departureDateTime"),
                        date.plusDays(1).atStartOfDay()
                )
        );
    }

    @CacheEvict(value = CacheKeys.TRIP, key = CacheKeys.TRIP_ALL)
    public void clearTripListCache() {}

}