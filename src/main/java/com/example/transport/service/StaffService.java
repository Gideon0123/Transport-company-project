package com.example.transport.service;

import com.example.transport.dto.*;
import com.example.transport.payload.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface StaffService {
    StaffResponseDTO createStaff(CreateStaffRequestDTO dto);
    PagedResponse<StaffSummaryDTO> getPagedStaffs(int page, int size, String sortBy);
    StaffResponseDTO getStaff(Long id);
    StaffResponseDTO updateStaff(Long id, UpdateStaffRequestDTO dto);
    void deleteStaff(Long id);
    Page<StaffResponseDTO> searchStaff(
            String keyword,
            String roleType,
            String nin,
            String bankName,
            String bankAccountNo,
            BigDecimal salary,
            Pageable pageable);
    Page<StaffResponseDTO> getDrivers(int page, int size, String sortBy);
    Page<StaffResponseDTO> getTicketers(int page, int size, String sortBy);
    Page<StaffResponseDTO> getManagers(int page, int size, String sortBy);
    Page<StaffResponseDTO> getAdmins(int page, int size, String sortBy);
}
