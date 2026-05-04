package com.example.transport.controller;

import com.example.transport.dto.*;
import com.example.transport.payload.ApiResponse;
import com.example.transport.payload.PagedResponse;
import com.example.transport.service.StaffService;
import com.example.transport.util.TraceIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/staffs")
@RequiredArgsConstructor
@CrossOrigin
public class StaffController {

    private final StaffService staffService;

    //CREATE STAFF
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<StaffResponseDTO>> createStaff(
            @Valid @RequestBody CreateStaffRequestDTO dto,
            HttpServletRequest request
    ) {
        StaffResponseDTO staff = staffService.createStaff(dto);

        return ResponseEntity.ok(
                ApiResponse.<StaffResponseDTO>builder()
                        .success(true)
                        .message("Staff Created successfully")
                        .statusCode(201)
                        .data(staff)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //GET ALL STAFFS
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<StaffSummaryDTO>>> getPagedStaffs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "staffId") String sortBy,
            HttpServletRequest request
    ) {

        int adjustedPage = Math.max(page - 1, 0);
        PagedResponse<StaffSummaryDTO> staffs = staffService.getPagedStaffs(adjustedPage, size, sortBy);
        PagedResponse<StaffSummaryDTO> response = PagedResponse.<StaffSummaryDTO>builder()
                .content(staffs.getContent())
                .size(staffs.getSize())
                .page(staffs.getPage())
                .first(staffs.isFirst())
                .last(staffs.isLast())
                .totalElements(staffs.getTotalElements())
                .totalPages(staffs.getTotalPages())
                .build();

        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<StaffSummaryDTO>>builder()
                        .success(true)
                        .message("Staffs fetched successfully")
                        .statusCode(200)
                        .data(response)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //GET STAFF
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StaffResponseDTO>> getStaff(@PathVariable Long id, HttpServletRequest request) {
        StaffResponseDTO staff = staffService.getStaff(id);

        return ResponseEntity.ok(
                ApiResponse.<StaffResponseDTO>builder()
                        .success(true)
                        .message("Staff fetched successfully")
                        .statusCode(200)
                        .data(staff)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //UPDATE
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StaffResponseDTO>> updateStaff(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStaffRequestDTO dto,
            HttpServletRequest request
    ) {
        StaffResponseDTO staff = staffService.updateStaff(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<StaffResponseDTO>builder()
                        .success(true)
                        .message("Staff Updated successfully")
                        .statusCode(200)
                        .data(staff)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //DELETE
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteStaff(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        staffService.deleteStaff(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Staff deleted successfully")
                        .statusCode(200)
                        .data(null)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //SEARCH
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<StaffResponseDTO>>> searchStaff(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String roleType,
            @RequestParam(required = false) String nin,
            @RequestParam(required = false) String bankName,
            @RequestParam(required = false) String bankAccountNo,
            @RequestParam(required = false) BigDecimal salary,

            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "staffId") String sortBy,
            HttpServletRequest request
    ) {

        //Convert to Spring format (0-based)
        int adjustedPage = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by(sortBy));

        Page<StaffResponseDTO> staffs = staffService.searchStaff(
                keyword,
                roleType,
                nin,
                bankName,
                bankAccountNo,
                salary,
                pageable
        );

        //Convert back to 1-based
        PagedResponse<StaffResponseDTO> response = PagedResponse.<StaffResponseDTO>builder()
                .content(staffs.getContent())
                .page(staffs.getNumber() + 1)
                .size(staffs.getSize())
                .totalElements(staffs.getTotalElements())
                .totalPages(staffs.getTotalPages())
                .first(staffs.isFirst())
                .last(staffs.isLast())
                .build();

        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<StaffResponseDTO>>builder()
                        .success(true)
                        .message("Trips fetched successfully")
                        .statusCode(200)
                        .data(response)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //GET DRIVERS
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/drivers")
    public ResponseEntity<ApiResponse<PagedResponse<StaffResponseDTO>>> getDrivers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "staffId") String sortBy,
            HttpServletRequest request
    ) {
        int adjustedPage = Math.max(page - 1, 0);
        Page<StaffResponseDTO> driversPage = staffService.getDrivers(adjustedPage, size, sortBy);
        PagedResponse<StaffResponseDTO> response = PagedResponse.<StaffResponseDTO>builder()
                .content(driversPage.getContent())
                .size(driversPage.getSize())
                .page(driversPage.getNumber() + 1)
                .first(driversPage.isFirst())
                .last(driversPage.isLast())
                .totalElements(driversPage.getTotalElements())
                .totalPages(driversPage.getTotalPages())
                .build();

        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<StaffResponseDTO>>builder()
                        .success(true)
                        .message("Drivers fetched successfully")
                        .statusCode(200)
                        .data(response)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //GET TICKETERS
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/ticketers")
    public ResponseEntity<ApiResponse<PagedResponse<StaffResponseDTO>>> getTicketers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "staffId") String sortBy,
            HttpServletRequest request
    ) {
        int adjustedPage = Math.max(page - 1, 0);
        Page<StaffResponseDTO> ticketersPage = staffService.getTicketers(adjustedPage, size, sortBy);
        PagedResponse<StaffResponseDTO> response = PagedResponse.<StaffResponseDTO>builder()
                .content(ticketersPage.getContent())
                .size(ticketersPage.getSize())
                .page(ticketersPage.getNumber() + 1)
                .first(ticketersPage.isFirst())
                .last(ticketersPage.isLast())
                .totalElements(ticketersPage.getTotalElements())
                .totalPages(ticketersPage.getTotalPages())
                .build();

        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<StaffResponseDTO>>builder()
                        .success(true)
                        .message("Ticketers fetched successfully")
                        .statusCode(200)
                        .data(response)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //GET MANAGERS
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/managers")
    public ResponseEntity<ApiResponse<PagedResponse<StaffResponseDTO>>> getManagers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "staffId") String sortBy,
            HttpServletRequest request
    ) {
        int adjustedPage = Math.max(page - 1, 0);
        Page<StaffResponseDTO> managersPage = staffService.getManagers(adjustedPage, size, sortBy);
        PagedResponse<StaffResponseDTO> response = PagedResponse.<StaffResponseDTO>builder()
                .content(managersPage.getContent())
                .size(managersPage.getSize())
                .page(managersPage.getNumber() + 1)
                .first(managersPage.isFirst())
                .last(managersPage.isLast())
                .totalElements(managersPage.getTotalElements())
                .totalPages(managersPage.getTotalPages())
                .build();

        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<StaffResponseDTO>>builder()
                        .success(true)
                        .message("Managers fetched successfully")
                        .statusCode(200)
                        .data(response)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //GET ADMINS
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/admins")
    public ResponseEntity<ApiResponse<PagedResponse<StaffResponseDTO>>> getAdmins(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "staffId") String sortBy,
            HttpServletRequest request
    ) {
        int adjustedPage = Math.max(page - 1, 0);
        Page<StaffResponseDTO> adminsPage = staffService.getAdmins(adjustedPage, size, sortBy);
        PagedResponse<StaffResponseDTO> response = PagedResponse.<StaffResponseDTO>builder()
                .content(adminsPage.getContent())
                .size(adminsPage.getSize())
                .page(adminsPage.getNumber() + 1)
                .first(adminsPage.isFirst())
                .last(adminsPage.isLast())
                .totalElements(adminsPage.getTotalElements())
                .totalPages(adminsPage.getTotalPages())
                .build();

        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<StaffResponseDTO>>builder()
                        .success(true)
                        .message("Admins fetched successfully")
                        .statusCode(200)
                        .data(response)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
