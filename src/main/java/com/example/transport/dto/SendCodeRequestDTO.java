package com.example.transport.dto;

import lombok.Data;

@Data
public class SendCodeRequestDTO {
    private String email;
    private String phone;
}