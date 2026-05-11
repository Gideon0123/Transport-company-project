package com.example.transport.dto;

import com.example.transport.enums.RoleType;
import com.example.transport.enums.UserStatus;
import lombok.Data;

@Data
public class UpdateUserRequestDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNo;
    private RoleType roleType;
    private UserStatus userStatus;

}
