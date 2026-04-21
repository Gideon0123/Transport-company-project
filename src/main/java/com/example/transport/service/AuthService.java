package com.example.transport.service;

import com.example.transport.dto.AuthResponseDTO;
import com.example.transport.dto.LoginRequestDTO;
import com.example.transport.dto.RegisterRequestDTO;
import com.example.transport.enums.UserStatus;
import com.example.transport.enums.UserType;
import com.example.transport.exception.BadRequestException;
import com.example.transport.exception.InvalidCredentialsException;
import com.example.transport.exception.ResourceNotFoundException;
import com.example.transport.model.RefreshToken;
import com.example.transport.model.User;
import com.example.transport.repository.UserRepository;
import com.example.transport.util.CacheKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
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
    public AuthResponseDTO register(RegisterRequestDTO request) {
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
        emailService.customerSignupMail(user);

        return new AuthResponseDTO(accessToken, refreshToken.getToken());
    }


    public AuthResponseDTO login(LoginRequestDTO request) {


        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.create(user);

        return new AuthResponseDTO(accessToken, refreshToken.getToken());

    }

    public AuthResponseDTO refresh(String refreshTokenStr) {

        if (refreshTokenStr == null) {
            throw new BadRequestException("Refresh Token Required");
        }

        RefreshToken oldToken = refreshTokenService.verify(refreshTokenStr);

        refreshTokenService.delete(oldToken);

        RefreshToken newToken = refreshTokenService.create(oldToken.getUser());
        String accessToken = jwtService.generateAccessToken(oldToken.getUser());

        return new AuthResponseDTO(accessToken, newToken.getToken());
    }

    public void logout(String refreshTokenStr) {
        RefreshToken token = refreshTokenService.verify(refreshTokenStr);
        refreshTokenService.revoke(token);
    }
}
