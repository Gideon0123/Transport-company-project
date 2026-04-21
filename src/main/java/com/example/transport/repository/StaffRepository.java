package com.example.transport.repository;

import com.example.transport.dto.StaffSummaryDTO;
import com.example.transport.enums.RoleType;
import com.example.transport.model.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface StaffRepository extends JpaRepository<Staff, Long>, JpaSpecificationExecutor<Staff> {
    Page<Staff> findByRoleType(RoleType roleType, Pageable pageable);

    @EntityGraph(attributePaths = {"vehicle", "vehicle.driver"})
    Page<Staff> findByDeletedFalse(Pageable pageable);

    @Query("""
    SELECT new com.example.transport.dto.StaffSummaryDTO(
        s.staffId,
        s.roleType,
        s.nin,
        s.bankName,
        s.status
    )
    FROM Staff s
    WHERE s.deleted = false
""")
    Page<StaffSummaryDTO> findAllStaffOptimized(Pageable pageable);
}
