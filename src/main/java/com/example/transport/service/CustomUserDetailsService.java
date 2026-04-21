package com.example.transport.service;

import com.example.transport.dto.CachedUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserCacheService userCacheService;

    @Override
    public UserDetails loadUserByUsername(String email) {

        CachedUserDTO cachedUser = userCacheService.getCachedUser(email);

        return new org.springframework.security.core.userdetails.User(
                cachedUser.getEmail(),
                cachedUser.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + cachedUser.getRole()))
        );
    }
}