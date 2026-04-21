package com.example.transport.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateStaffRequestDTO {

    private Long userId;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNo;
    private String nin;
    private String roleType;

    private String guarantorName;
    private String guarantorAddress;
    private String guarantorPhone;
    private String guarantorEmail;

    private String bankName;
    private String bankAccountNo;
    private BigDecimal salary;
}
