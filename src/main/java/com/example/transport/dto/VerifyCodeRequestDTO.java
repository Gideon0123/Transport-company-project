package com.example.transport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyCodeRequestDTO {
    private String code;
    private String email;
    private String phone;


}