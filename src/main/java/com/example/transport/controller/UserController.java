package com.example.transport.controller;

import com.example.transport.dto.*;
import com.example.transport.payload.ApiResponse;
import com.example.transport.payload.PagedResponse;
import com.example.transport.service.UserService;
import com.example.transport.util.TraceIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final UserService userService;

    //GET ALL USER
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<UserSummaryDTO>>> getPagedUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "userId") String sortBy,
            HttpServletRequest request
    ) {
        int adjustedPage = Math.max(page - 1, 0);
        PagedResponse<UserSummaryDTO> users = userService.getPagedUsers(adjustedPage, size, sortBy);
        PagedResponse<UserSummaryDTO> response = PagedResponse.<UserSummaryDTO>builder()
                .content(users.getContent())
                .size(users.getSize())
                .page(users.getPage())
                .first(users.isFirst())
                .last(users.isLast())
                .totalElements(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .build();

        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<UserSummaryDTO>>builder()
                        .success(true)
                        .message("Users fetched successfully")
                        .statusCode(200)
                        .data(response)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //GET USER
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUser(
            @PathVariable Long id, HttpServletRequest request
    ) {
        UserResponseDTO user = userService.getUser(id);

        return ResponseEntity.ok(
                ApiResponse.<UserResponseDTO>builder()
                        .success(true)
                        .message("User fetched successfully")
                        .statusCode(200)
                        .data(user)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //UPDATE USER
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequestDTO dto,
            HttpServletRequest request) {
        UserResponseDTO user = userService.updateUser(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<UserResponseDTO>builder()
                        .success(true)
                        .message("User Updated successfully")
                        .statusCode(200)
                        .data(user)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //DELETE USER
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteUser(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
//        CookieUtil.clearCookies(response);
        userService.deleteUser(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("User deleted successfully")
                        .statusCode(200)
                        .data(null)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //SEARCH USER
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponseDTO>>> searchUser(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNo,
            @RequestParam(required = false) String userType,
            @RequestParam(required = false) String userStatus,
            @RequestParam(required = false) String roleType,

            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "userId") String sortBy,
            HttpServletRequest request
    ) {
        //Convert to Spring format (0-based)
        int adjustedPage = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by(sortBy));

        Page<UserResponseDTO> users = userService.searchUser(
                keyword,
                firstName,
                lastName,
                email,
                phoneNo,
                userType,
                userStatus,
                roleType,
                pageable);

        //Convert back to 1-based
        PagedResponse<UserResponseDTO> response = PagedResponse.<UserResponseDTO>builder()
                .content(users.getContent())
                .page(users.getNumber() + 1)
                .size(users.getSize())
                .totalElements(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .first(users.isFirst())
                .last(users.isLast())
                .build();

        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<UserResponseDTO>>builder()
                        .success(true)
                        .message("Users fetched successfully")
                        .statusCode(200)
                        .data(response)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //GET ME
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getCurrentUser(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
            HttpServletRequest request
    ) {

        UserResponseDTO response = userService.getCurrentUser(userDetails.getUsername());

        return ResponseEntity.ok(
                ApiResponse.<UserResponseDTO>builder()
                        .success(true)
                        .message("User fetched successfully")
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
