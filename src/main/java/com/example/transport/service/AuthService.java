package com.example.transport.service;

import com.example.transport.dto.*;
import com.example.transport.enums.UserStatus;
import com.example.transport.enums.UserType;
import com.example.transport.exception.AuthenticationException;
import com.example.transport.exception.BadRequestException;
import com.example.transport.exception.InvalidCredentialsException;
import com.example.transport.model.RefreshToken;
import com.example.transport.model.User;
import com.example.transport.repository.UserRepository;
import com.example.transport.util.CacheKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;

    @CacheEvict(value = CacheKeys.USER, allEntries = true)
    public LoginResponseDTO register(RegisterRequestDTO request) {
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNo(request.getPhoneNo())
                .userType(UserType.CUSTOMER)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.create(user);

        AuthResponseDTO authResponse = AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();

        UserResponseDTO userResponse = UserResponseDTO.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .userStatus(user.getStatus())
                .userType(user.getUserType())
                .roleType(user.getRoleType())
                .build();
        try {
            emailService.customerSignupMail(user);
        } catch (Exception e) {
            System.err.println("Email failed: " + e.getMessage());
        }

        return LoginResponseDTO.builder()
                .authResponse(authResponse)
                .userResponse(userResponse)
                .build();

    }

    public LoginResponseDTO login(LoginRequestDTO request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.create(user);

        AuthResponseDTO authResponse = AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();

        UserResponseDTO userResponse = UserResponseDTO.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .userStatus(user.getStatus())
                .userType(user.getUserType())
                .roleType(user.getRoleType())
                .build();

        return LoginResponseDTO.builder()
                .authResponse(authResponse)
                .userResponse(userResponse)
                .build();

    }

    public LoginResponseDTO refresh(String refreshTokenStr, UserDetails userDetails) {

        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("User Not Logged In!!!"));

        if (refreshTokenStr == null) {
            throw new BadRequestException("Refresh Token Required");
        }

        RefreshToken oldToken = refreshTokenService.verify(refreshTokenStr);

        refreshTokenService.delete(oldToken);

        RefreshToken newToken = refreshTokenService.create(oldToken.getUser());
        String accessToken = jwtService.generateAccessToken(oldToken.getUser());

        AuthResponseDTO authResponse = AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(newToken.getToken())
                .build();

        UserResponseDTO userResponse = UserResponseDTO.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .userStatus(user.getStatus())
                .userType(user.getUserType())
                .roleType(user.getRoleType())
                .build();

        return LoginResponseDTO.builder()
                .authResponse(authResponse)
                .userResponse(userResponse)
                .build();

    }

    public void logout(String refreshTokenStr) {
        RefreshToken token = refreshTokenService.verify(refreshTokenStr);
        refreshTokenService.revoke(token);
    }
}
