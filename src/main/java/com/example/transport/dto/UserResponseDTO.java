package com.example.transport.dto;

import com.example.transport.enums.RoleType;
import com.example.transport.enums.UserStatus;
import com.example.transport.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNo;
    private UserType userType;
    private RoleType roleType;
    private UserStatus userStatus;

}