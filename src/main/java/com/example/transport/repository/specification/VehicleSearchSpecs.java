package com.example.transport.repository.specification;

import com.example.transport.model.Vehicle;
import org.springframework.data.jpa.domain.Specification;

public class VehicleSearchSpecs {

    public static Specification<Vehicle> hasDriver(String driver) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("driver"), driver);
    }

    public static Specification<Vehicle> hasVehiclePlate(String vehiclePlate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("vehiclePlate"), vehiclePlate);
    }

    public static Specification<Vehicle> hasVehicleType(String vehicleType) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("vehicleType"), vehicleType);
    }
}
