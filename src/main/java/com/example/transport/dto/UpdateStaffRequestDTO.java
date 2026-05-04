package com.example.transport.dto;

import com.example.transport.enums.RoleType;
import com.example.transport.enums.UserStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateStaffRequestDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNo;

    private RoleType roleType;
    private UserStatus userStatus;
    private String nin;

    private String guarantorName;
    private String guarantorAddress;
    private String guarantorPhone;
    private String guarantorEmail;

    private String bankName;
    private String bankAccountNo;
    private BigDecimal salary;
    private Long userId;
}
