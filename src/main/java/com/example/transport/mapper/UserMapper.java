package com.example.transport.mapper;

import com.example.transport.dto.UserResponseDTO;
import com.example.transport.enums.UserStatus;
import com.example.transport.enums.UserType;
import com.example.transport.model.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserMapper {

    public static UserResponseDTO toDTO(User user) {

        if (user == null) return null;

        UserResponseDTO dto = new UserResponseDTO();

        dto.setUserId(user.getUserId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNo(user.getPhoneNo());
        dto.setUserType(user.getStaff() == null ? UserType.CUSTOMER : user.getUserType());
        dto.setUserStatus(UserStatus.ACTIVE);

        dto.setRoleType(
                user.getStaff() != null ? user.getStaff().getRoleType() : null
        );

        return dto;
    }
}