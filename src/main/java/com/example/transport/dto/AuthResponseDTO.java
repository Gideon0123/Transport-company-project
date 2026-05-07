package com.example.transport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthResponseDTO {
    private String accessToken;
    private String refreshToken;
}
