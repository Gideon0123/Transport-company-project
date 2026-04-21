package com.example.transport.service;

import com.example.transport.dto.*;
import com.example.transport.payload.PagedResponse;

import java.util.List;

public interface UserService {

//    Page<UserSummaryDTO> getPagedUsers(int page, int size, String sortBy);
    PagedResponse<UserSummaryDTO> getPagedUsers(int page, int size, String sortBy);
    UserResponseDTO getUser(Long id);
    UserResponseDTO updateUser(Long id, UpdateUserRequestDTO dto);
    void deleteUser(Long id);
    List<UserResponseDTO> searchUser(String firstName, String lastName, String email, String phoneNo);
    UserResponseDTO getCurrentUser(String email);
    void changePassword(ChangePasswordRequestDTO request);
}
