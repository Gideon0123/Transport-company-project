package com.example.transport.mapper;

import com.example.transport.dto.StaffResponseDTO;
import com.example.transport.dto.UserResponseDTO;
import com.example.transport.model.Staff;
import com.example.transport.model.User;
import lombok.Builder;
import lombok.Data;
import org.jspecify.annotations.NonNull;

@Data
@Builder
public class StaffMapper {

    public static StaffResponseDTO toDTO(Staff staff) {

        User user = staff.getUser();

        UserResponseDTO userDTO = new UserResponseDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhoneNo(user.getPhoneNo());
        userDTO.setUserType(user.getUserType());
        userDTO.setUserStatus(user.getStatus());
        userDTO.setRoleType(user.getRoleType());

        return getStaffResponseDTO(staff, userDTO);
    }

    private static @NonNull StaffResponseDTO getStaffResponseDTO(Staff staff, UserResponseDTO userDTO) {
        StaffResponseDTO dto = new StaffResponseDTO();
        dto.setStaffId(staff.getStaffId());
        dto.setNin(staff.getNin());
        dto.setGuarantorName(staff.getGuarantorName());
        dto.setGuarantorAddress(staff.getGuarantorAddress());
        dto.setGuarantorEmail(staff.getGuarantorEmail());
        dto.setGuarantorPhone(staff.getGuarantorPhone());
        dto.setBankName(staff.getBankName());
        dto.setBankAccountNo(staff.getBankAccountNo());
        dto.setSalary(staff.getSalary());

        dto.setUser(userDTO);
        return dto;
    }
}
