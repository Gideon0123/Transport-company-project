package com.example.transport.service;

import com.example.transport.dto.*;
import com.example.transport.enums.RoleType;
import com.example.transport.enums.VehicleStatus;
import com.example.transport.enums.VehicleType;
import com.example.transport.exception.BadRequestException;
import com.example.transport.exception.ResourceNotFoundException;
import com.example.transport.mapper.VehicleMapper;
import com.example.transport.model.Staff;
import com.example.transport.model.Vehicle;
import com.example.transport.payload.PagedResponse;
import com.example.transport.repository.StaffRepository;
import com.example.transport.repository.TripRepository;
import com.example.transport.repository.VehicleRepository;
import com.example.transport.repository.specification.GenericSearchSpecification;
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
import java.util.HashMap;
import java.util.Map;

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

        if (dto.getVehiclePlate() == null) {
            throw new BadRequestException("Cannot Register Vehicle without Plate Number");
        } else if (dto.getVehicleType() == null) {
            throw new BadRequestException("Please Select a Vehicle Type");
        }

        if (vehicleRepository.existsByVehiclePlate(dto.getVehiclePlate())) {
            throw new BadRequestException("Vehicle plate already exists");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setVehiclePlate(dto.getVehiclePlate());

        if (vehicle.isDeleted()) {
            throw new BadRequestException("Cannot assign deleted vehicle");
        }

        vehicle.setVehicleType(
                VehicleType.valueOf(dto.getVehicleType().toUpperCase()));

        if (dto.getDriverId() != null) {

            Staff driver = staffRepository.findById(dto.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

            boolean driverAlreadyAssigned = vehicleRepository.existsByDriver(driver);

            if (driverAlreadyAssigned) {
                throw new BadRequestException("Driver already assigned to another vehicle");
            }

            if (driver.getRoleType() != RoleType.DRIVER) {
                throw new BadRequestException("Assigned staff is not a DRIVER");
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

        VehicleStatus current = dto.getVehicleStatus();
        if (current == VehicleStatus.DELETED) {
            throw new BadRequestException("Cannot Update a Deleted Vehicle");
        }

        Vehicle existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        if (dto.getVehiclePlate() != null) {
            existingVehicle.setVehiclePlate(dto.getVehiclePlate());
        }

        if (dto.getVehicleType() != null) {
            existingVehicle.setVehicleType(VehicleType.valueOf(dto.getVehicleType()));
        }

        if (dto.getVehicleStatus() != null) {
            existingVehicle.setStatus(VehicleStatus.valueOf(String.valueOf(dto.getVehicleStatus())));
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
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        //Check ACTIVE trips (future trips)
        boolean hasActiveTrips = tripRepository
                .existsByVehicleAndDepartureDateTimeAfter(vehicle, LocalDateTime.now());

        if (hasActiveTrips) {
            throw new RuntimeException("Cannot delete vehicle with active trips");
        }

        //Soft delete
        vehicle.setDeleted(true);
        vehicle.setStatus(VehicleStatus.DELETED);

        vehicleRepository.deleteById(id);
    }

    @Override
    public Page<VehicleResponseDTO> searchVehicles(
            String keyword,
            Long vehicleId,
            String vehiclePlate,
            String vehicleType,
            String vehicleStatus,
            Long driverId,
            String firstName,
            String lastName,
            Pageable pageable
    ) {

        Map<String, Object> filters = new HashMap<>();

        if (vehicleId != null) {
            filters.put("vehicleId", vehicleId);
        }

        if (vehiclePlate != null && !vehiclePlate.isEmpty()) {
            filters.put("vehiclePlate", vehiclePlate);
        }

        if (vehicleType != null && !vehicleType.isEmpty()) {
            filters.put("vehicleType", VehicleType.valueOf(vehicleType.toUpperCase().trim()));
        }

        if (vehicleStatus != null && !vehicleStatus.isEmpty()) {
            filters.put("status", VehicleStatus.valueOf(vehicleStatus.toUpperCase().trim()));
        }
        // Driver's search params

        if (driverId != null) {
            filters.put("driver.id", driverId);
        }

        if (firstName != null && !firstName.isEmpty()) {
            filters.put("driver.user.firstName", firstName);
        }

        if (lastName != null && !lastName.isEmpty()) {
            filters.put("driver.user.lastName", lastName);
        }

        Specification<Vehicle> spec =
                new GenericSearchSpecification<Vehicle>().build(filters);

        if (keyword != null && keyword.length() >= 3) {
            Specification<Vehicle> keywordSpec =
                    VehicleSearchSpecs.keywordSearch(keyword);

            spec = (spec == null)
                    ? keywordSpec
                    : spec.and(keywordSpec);
        }

        Page<Vehicle> vehiclePage =
                vehicleRepository.findAll(spec, pageable);

        return vehiclePage.map(VehicleMapper::toDTO);
    }

    @CacheEvict(value = CacheKeys.VEHICLE, key = CacheKeys.VEHICLE_ALL)
    public void clearVehicleListCache() {
        // optional manual clearing
    }

}
