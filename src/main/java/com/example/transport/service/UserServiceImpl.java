package com.example.transport.service;

import com.example.transport.dto.*;
import com.example.transport.enums.UserStatus;
import com.example.transport.exception.ResourceNotFoundException;
import com.example.transport.mapper.UserMapper;
import com.example.transport.model.User;
import com.example.transport.payload.PagedResponse;
import com.example.transport.repository.RefreshTokenRepository;
import com.example.transport.repository.StaffRepository;
import com.example.transport.repository.UserRepository;
import com.example.transport.repository.specification.UserSearchSpecs;
import com.example.transport.util.CacheKeys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StaffRepository staffRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Cacheable(value = CacheKeys.USER, key = "#page + '-' + #size + '-' + #sortBy")
    public PagedResponse<UserSummaryDTO> getPagedUsers(int page, int size, String sortBy) {

        System.out.println("DB HIT: Fetching users from database...");

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<UserSummaryDTO> userPage = userRepository.findAllUsersOptimized(pageable);

        return new PagedResponse<>(userPage);
    }

    @Override
    @Cacheable(value = CacheKeys.USER, key = "#id")
    public UserResponseDTO getUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserMapper.toDTO(user);
    }

    @Override
    @CacheEvict(value = CacheKeys.USER, allEntries = true)
    public UserResponseDTO updateUser(Long id, UpdateUserRequestDTO dto) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (dto.getFirstName() != null) {
            existingUser.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            existingUser.setLastName(dto.getLastName());
        }

        if (dto.getEmail() != null) {
            existingUser.setEmail(dto.getEmail());
        }

        if (dto.getPhoneNo() != null) {
            existingUser.setPhoneNo(dto.getPhoneNo());
        }

        if (dto.getUserStatus() != null) {
            existingUser.setStatus(dto.getUserStatus());
        }

        return UserMapper.toDTO(userRepository.save(existingUser));
    }

    @Override
    @CacheEvict(value = CacheKeys.USER, allEntries = true)
    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot Delete: User not found");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        refreshTokenRepository.deleteByUser(user);

        if (user.getStaff() != null) {
            staffRepository.delete(user.getStaff());
            user.setDeleted(true);
            user.setStatus(UserStatus.INACTIVE);
        }

        user.setDeleted(true);
        user.setStatus(UserStatus.INACTIVE);

        userRepository.delete(user);
    }

    @Override
    public List<UserResponseDTO> searchUser(String firstName, String lastName, String email, String phoneNo) {

        Specification<User> spec = Specification.where((Specification<User>) null);

        if (firstName != null && !firstName.isEmpty()) {
            spec = spec.and(UserSearchSpecs.hasFirstName(firstName));
        }
        if (lastName != null && !lastName.isEmpty()) {
            spec = spec.and(UserSearchSpecs.hasLastName(lastName));
        }
        if (email != null && !email.isEmpty()) {
            spec = spec.and(UserSearchSpecs.hasEmail(email));
        }
        if (phoneNo != null && !phoneNo.isEmpty()) {
            spec = spec.and(UserSearchSpecs.hasPhoneNo(phoneNo));
        }

        List<User> users = userRepository.findAll(spec, Sort.by("firstName"));
        return users.stream()
                .map(UserMapper::toDTO)
                .toList();

    }

    @Override
    public UserResponseDTO getCurrentUser(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserMapper.toDTO(user);
    }

    @Override
    public void changePassword(ChangePasswordRequestDTO request) {

        String email = Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        //check old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        //set new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }

}
