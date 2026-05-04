package com.example.transport.dto;

import lombok.Data;

@Data
public class UserSimpleDTO {

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNo;
}
