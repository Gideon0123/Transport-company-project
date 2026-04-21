package com.example.transport.repository;

import com.example.transport.model.User;
import com.example.transport.model.VerificationToken;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

//import java.lang.ScopedValue;
import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    @Query("""
        SELECT vt FROM VerificationToken vt
        WHERE vt.code = :code
        AND (
            (:email IS NOT NULL AND vt.email = :email)
            OR
            (:phone IS NOT NULL AND vt.phone = :phone)
        )
    """)
    Optional<VerificationToken> findByCodeAndEmailOrPhone(
            @Param("code") String code,
            @Param("email") String email,
            @Param("phone") String phone
    );

    @Query("""
    SELECT vt FROM VerificationToken vt
    WHERE (vt.email = :email OR vt.phone = :phone)
    ORDER BY vt.id DESC
""")
    Optional<VerificationToken> findLatestByEmailOrPhone(String email, String phone);

    Optional<VerificationToken> findTopByCodeAndEmailOrderByLastSentAtDesc(
            String code, String email
    );

    Optional<VerificationToken> findTopByCodeAndPhoneOrderByLastSentAtDesc(
            String code, String phone
    );

    @Modifying
    @Query("DELETE FROM VerificationToken t WHERE t.email = :email")
    void deleteAllByEmail(String email);

    @Modifying
    @Query("DELETE FROM VerificationToken t WHERE t.phone = :phone")
    void deleteAllByPhone(String phone);

    @Modifying
    @Query("DELETE FROM VerificationToken vt WHERE vt.expiryDate < :now OR vt.used = true")
    void deleteAllExpired(LocalDateTime now);

}
