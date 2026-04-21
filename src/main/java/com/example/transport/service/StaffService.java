package com.example.transport.service;

import com.example.transport.dto.*;
import com.example.transport.payload.PagedResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StaffService {
    StaffResponseDTO createStaff(CreateStaffRequestDTO dto);
//    Page<StaffSummaryDTO> getPagedStaffs(int page, int size, String sortBy);
    PagedResponse<StaffSummaryDTO> getPagedStaffs(int page, int size, String sortBy);
    StaffResponseDTO getStaff(Long id);
    StaffResponseDTO updateStaff(Long id, UpdateStaffRequestDTO dto);
    void deleteStaff(Long id);
    List<StaffResponseDTO> searchStaff(String userType, String RoleType, String nin, String bankAccountNo);
    Page<StaffResponseDTO> getDrivers(int page, int size, String sortBy);
    Page<StaffResponseDTO> getTicketers(int page, int size, String sortBy);
    Page<StaffResponseDTO> getManagers(int page, int size, String sortBy);
    Page<StaffResponseDTO> getAdmins(int page, int size, String sortBy);
}
