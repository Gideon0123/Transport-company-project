package com.example.transport.service;

import com.example.transport.dto.*;
import com.example.transport.enums.UserStatus;
import com.example.transport.exception.AuthenticationException;
import com.example.transport.exception.InvalidCredentialsException;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public Page<UserResponseDTO> searchUser(
            String keyword,
            String firstName,
            String lastName,
            String email,
            String phoneNo,
            String userType ,
            String userStatus,
            String roleType,
            Pageable pageable) {

        Specification<User> spec = Specification.allOf();

        if (keyword != null && keyword.length() >= 3) {
            spec = spec.and(UserSearchSpecs.keywordSearch(keyword));
        }
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

        if (userType != null && !userType.isEmpty()) {
            spec = spec.and(UserSearchSpecs.hasUserType(userType));
        }

        if (roleType != null && !roleType.isEmpty()) {
            spec = spec.and(UserSearchSpecs.hasRoleType(roleType));
        }

        if (userStatus != null && !userStatus.isEmpty()) {
            spec = spec.and(UserSearchSpecs.hasUserStatus(userStatus));
        }

        System.out.println("UserType is = " + userType);

        Page<User> users = userRepository.findAll(spec, pageable);
        return users.map(UserMapper::toDTO);

    }

    @Override
    public UserResponseDTO getCurrentUser(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserMapper.toDTO(user);
    }

    @Override
    public void changePassword(ChangePasswordRequestDTO request,UserDetails userDetails) {

        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("User Not Logged In!!!"));

        //check old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Old password is incorrect");
        }

        //set new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }

}
