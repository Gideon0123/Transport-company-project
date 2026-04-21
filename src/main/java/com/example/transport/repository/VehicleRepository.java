package com.example.transport.repository;

import com.example.transport.dto.VehicleSummaryDTO;
import com.example.transport.enums.VehicleStatus;
import com.example.transport.model.Staff;
import com.example.transport.model.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {
    boolean existsByVehiclePlate(String vehiclePlate);
//    Page<Vehicle> findByStatus(VehicleStatus status, Pageable pageable);
    boolean existsByDriver(Staff driver);

    @EntityGraph(attributePaths = {"vehicle", "vehicle.driver"})
    Page<Vehicle> findByDeletedFalse(Pageable pageable);

    @Query("""
    SELECT new com.example.transport.dto.VehicleSummaryDTO(
        v.vehicleId,
        v.vehiclePlate,
        v.vehicleType,
        v.status
    )
    FROM Vehicle v
    WHERE v.deleted = false
""")
    Page<VehicleSummaryDTO> findAllVehiclesOptimized(Pageable pageable);
}
