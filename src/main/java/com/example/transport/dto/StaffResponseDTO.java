package com.example.transport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffResponseDTO {

    private Long staffId;

//    private RoleType roleType;
//    private UserStatus status;
    private String nin;

    private String guarantorName;
    private String guarantorAddress;
    private String guarantorPhone;
    private String guarantorEmail;

    private String bankName;
    private String bankAccountNo;
    private BigDecimal salary;

    private UserResponseDTO user;
}
