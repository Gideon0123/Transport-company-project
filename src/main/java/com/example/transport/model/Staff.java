package com.example.transport.model;

import com.example.transport.enums.RoleType;
import com.example.transport.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Staff extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long staffId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @NotBlank
    private String nin;

    @NotBlank
    private String guarantorName;

    @NotBlank
    private String guarantorAddress;
    @NotBlank
    private String guarantorPhone;
    @NotBlank
    private String guarantorEmail;

    @NotBlank
    private String bankName;
    @NotBlank
    private String bankAccountNo;

    @NotNull
    @Positive
    private BigDecimal salary;

    @Column(nullable = false)
    private boolean deleted = false;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;
}