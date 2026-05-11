package com.example.transport.service;

import com.example.transport.dto.*;
import com.example.transport.payload.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {

//    Page<UserSummaryDTO> getPagedUsers(int page, int size, String sortBy);
    PagedResponse<UserSummaryDTO> getPagedUsers(int page, int size, String sortBy);
    UserResponseDTO getUser(Long id);
    UserResponseDTO updateUser(Long id, UpdateUserRequestDTO dto);
    void deleteUser(Long id);
    Page<UserResponseDTO> searchUser(String keyword,
                                     String firstName,
                                     String lastName,
                                     String email,
                                     String phoneNo,
                                     String userType,
                                     String userStatus,
                                     String roleType,
                                     Pageable pageable);
    UserResponseDTO getCurrentUser(String email);
    void changePassword(
            ChangePasswordRequestDTO request,
            UserDetails userDetails
    );
}
