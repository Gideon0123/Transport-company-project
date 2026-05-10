package com.example.transport.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotNull(message = "Cannot Validate Empty Credentials")
    private String email;

    @NotNull(message = "Cannot Validate Empty Credentials")
    private String password;
}
