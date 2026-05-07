package com.example.transport.dto;

import com.example.transport.enums.UserStatus;
import com.example.transport.enums.UserType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSummaryDTO {

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private UserType userType;
    private UserStatus status;

    public UserSummaryDTO(
            Long userId,
            String firstName,
            String lastName,
            String email,
            UserType userType,
            UserStatus status
    ) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userType = userType;
        this.status = status;
    }
}