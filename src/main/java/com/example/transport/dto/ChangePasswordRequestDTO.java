package com.example.transport.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangePasswordRequestDTO {

    @NotNull(message = "Old password is required")
    private String oldPassword;

    @NotNull(message = "Choose a new Password")
    private String newPassword;
}
