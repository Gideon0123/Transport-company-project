package com.example.transport.dto;

import com.example.transport.enums.UserStatus;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String email;
    private String password;

    private String firstName;
    private String lastName;
    private String phoneNo;

    private UserStatus userStatus;
}
