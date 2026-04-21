package com.example.transport.service;

import com.example.transport.dto.CachedUserDTO;
import com.example.transport.model.User;
import com.example.transport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCacheService {

    private final UserRepository userRepository;

    @Cacheable(value = "users", key = "#email")
    public CachedUserDTO getCachedUser(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String role = user.getStaff() != null
                ? user.getStaff().getRoleType().name()
                : "USER";

        return new CachedUserDTO(
                user.getEmail(),
                user.getPassword(),
                role
        );
    }
}
