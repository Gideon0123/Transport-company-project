package com.example.transport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ForgotPasswordRequestDTO {

    private String email;
    private String phone;
    private String code;
    private String newPassword;
}