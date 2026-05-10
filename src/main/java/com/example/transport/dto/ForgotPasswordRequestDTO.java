package com.example.transport.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ForgotPasswordRequestDTO {

    private String email;
    private String phone;

    @NotNull(message = "Code is Required!!")
    private String code;

    @NotNull(message = "Please Choose a new Password!!")
    private String newPassword;
}