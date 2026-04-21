package com.example.transport.dto;

import com.example.transport.enums.RoleType;
import com.example.transport.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StaffSummaryDTO {
    private Long staffId;
    private RoleType roleType;
    private String nin;
    private String bankName;
    private UserStatus status;

    public StaffSummaryDTO(Long staffId,
                           RoleType roleType,
                           String nin,
                           String bankName,
                           UserStatus status) {
        this.staffId = staffId;
        this.roleType = roleType;
        this.nin = nin;
        this.bankName = bankName;
        this.status = status;
    }
}