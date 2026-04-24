package com.example.transport.service;

import com.example.transport.dto.*;
import com.example.transport.enums.RoleType;
import com.example.transport.enums.VehicleStatus;
import com.example.transport.enums.VehicleType;
import com.example.transport.exception.ResourceNotFoundException;
import com.example.transport.mapper.VehicleMapper;
import com.example.transport.model.Staff;
import com.example.transport.model.Vehicle;
import com.example.transport.payload.PagedResponse;
import com.example.transport.repository.StaffRepository;
import com.example.transport.repository.TripRepository;
import com.example.transport.repository.VehicleRepository;
import com.example.transport.repository.specification.VehicleSearchSpecs;
import com.example.transport.util.CacheKeys;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Transactional
public class VehicleServiceImpl implements VehicleService{

    private final VehicleRepository vehicleRepository;
    private final StaffRepository staffRepository;
    private final TripRepository tripRepository;

    @Override
//    @CachePut(value = "vehicles", key = "#result.id")
    @CacheEvict(value = CacheKeys.VEHICLE, allEntries = true)
    public VehicleResponseDTO createVehicle(CreateVehicleRequestDTO dto) {

        if (vehicleRepository.existsByVehiclePlate(dto.getVehiclePlate())) {
            throw new RuntimeException("Vehicle plate already exists");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setVehiclePlate(dto.getVehiclePlate());

        if (vehicle.isDeleted()) {
            throw new RuntimeException("Cannot assign deleted vehicle");
        }

        if (dto.getVehicleType() != null) {
            vehicle.setVehicleType(
                    VehicleType.valueOf(dto.getVehicleType().toUpperCase()));
        }

        if (dto.getDriverId() != null) {

            Staff driver = staffRepository.findById(dto.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

            boolean driverAlreadyAssigned = vehicleRepository.existsByDriver(driver);

            if (driverAlreadyAssigned) {
                throw new RuntimeException("Driver already assigned to another vehicle");
            }

            if (driver.getRoleType() != RoleType.DRIVER) {
                throw new RuntimeException("Assigned staff is not a DRIVER");
            }

            vehicle.setDriver(driver);
        }

        //Save
        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        //Return DTO
        return VehicleMapper.toDTO(savedVehicle);
    }

    @Override
    @Cacheable(value = CacheKeys.VEHICLE, key = "#page + '-' + #size + '-' + #sortBy")
    public PagedResponse<VehicleSummaryDTO> getPagedVehicles(int page, int size, String sortBy) {

        System.out.println("DB HIT: Fetching vehicles from database...");

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<VehicleSummaryDTO> vehiclePage = vehicleRepository.findAllVehiclesOptimized(pageable);

        return new PagedResponse<>(vehiclePage);
    }

    @Override
    @Cacheable(value = CacheKeys.VEHICLE, key = "#id")
    public VehicleResponseDTO getVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle Not Found"));
        return VehicleMapper.toDTO(vehicle);
    }

    @Override
//    @CachePut(value = "vehicles", key = "#id")
    @CacheEvict(value = CacheKeys.VEHICLE, allEntries = true)
    public VehicleResponseDTO updateVehicle(Long id, UpdateVehicleRequestDTO dto) {

        Vehicle existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        if (dto.getVehiclePlate() != null) {
            existingVehicle.setVehiclePlate(dto.getVehiclePlate());
        }

        if (dto.getVehicleType() != null) {
            existingVehicle.setVehicleType(VehicleType.valueOf(dto.getVehicleType()));
        }

        if (dto.getVehicleStatus() != null) {
            existingVehicle.setStatus(dto.getVehicleStatus());
        }

        if (dto.getDriverId() != null) {

            Staff driver = staffRepository.findById(dto.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

            existingVehicle.setDriver(driver);
        }

        Vehicle saved = vehicleRepository.save(existingVehicle);

        return VehicleMapper.toDTO(saved);
    }

    @Override
    @CacheEvict(value = CacheKeys.VEHICLE, allEntries = true)
    public void deleteVehicle(Long id) {

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        //Check ACTIVE trips (future trips)
        boolean hasActiveTrips = tripRepository
                .existsByVehicleAndDepartureDateTimeAfter(vehicle, LocalDateTime.now());

        if (hasActiveTrips) {
            throw new RuntimeException("Cannot delete vehicle with active trips");
        }

        //Soft delete
        vehicle.setDeleted(true);
        vehicle.setStatus(VehicleStatus.DELETED);

        vehicleRepository.save(vehicle);
    }

    @Override
    public Page<VehicleResponseDTO> searchVehicles(String keyword,
                                                   Long driverId,
                                                   String vehiclePlate,
                                                   String vehicleType,
                                                   String vehicleStatus,
                                                   Pageable pageable) {

        Specification<Vehicle> spec = Specification.allOf();

        if (keyword != null && keyword.length() >= 3) {
            spec = spec.and(VehicleSearchSpecs.keywordSearch(keyword));
        }
        if (driverId != null) {
            spec = spec.and(VehicleSearchSpecs.hasDriverId(driverId));
        }

        if (vehiclePlate != null && !vehiclePlate.isEmpty()) {
            spec = spec.and(VehicleSearchSpecs.hasVehiclePlate(vehiclePlate));
        }

        if (vehicleType != null && !vehicleType.isEmpty()) {
            spec = spec.and(VehicleSearchSpecs.hasVehicleType(vehicleType));
        }

        if (vehicleStatus != null && !vehicleStatus.isEmpty()) {
            spec = spec.and(VehicleSearchSpecs.hasVehicleStatus(vehicleStatus));
        }

        Page<Vehicle> vehicles = vehicleRepository.findAll(spec, pageable);

        return vehicles.map(VehicleMapper::toDTO);
    }

    @CacheEvict(value = CacheKeys.VEHICLE, key = CacheKeys.VEHICLE_ALL)
    public void clearVehicleListCache() {
        // optional manual clearing
    }

}
