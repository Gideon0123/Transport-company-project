package com.example.transport.repository;

import com.example.transport.dto.UserSummaryDTO;
import com.example.transport.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
//    boolean existsByPhoneNo(String phoneNo);

    @EntityGraph(attributePaths = {"vehicle", "vehicle.driver"})
    Page<User> findByDeletedFalse(Pageable pageable);

    @Query("""
    SELECT new com.example.transport.dto.UserSummaryDTO(
        u.userId,
        u.firstName,
        u.lastName,
        u.email,
        u.userType,
        u.status
    )
    FROM User u
    WHERE u.deleted = false
""")
    Page<UserSummaryDTO> findAllUsersOptimized(Pageable pageable);

    Optional<User> findByPhoneNo(String phoneNo);
}
