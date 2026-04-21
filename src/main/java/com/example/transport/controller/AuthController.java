package com.example.transport.controller;

import com.example.transport.dto.*;
import com.example.transport.payload.ApiResponse;
import com.example.transport.service.*;
import com.example.transport.util.CookieUtil;
import com.example.transport.util.TraceIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthService service;
    private final VerificationTokenService verificationService;
    private final UserService userService;
    private final JwtService jwtService;

    //REGISTER
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(
            @RequestBody RegisterRequestDTO request,
            HttpServletResponse response,
            HttpServletRequest httpRequest
    ) {

        AuthResponseDTO auth = service.register(request);

        CookieUtil.addAccessToken(response, auth.getAccessToken());
        CookieUtil.addRefreshToken(response, auth.getRefreshToken());

        return ResponseEntity.status(201).body(
                ApiResponse.builder()
                        .success(true)
                        .message("Signup successful")
                        .statusCode(201)
                        .data(null)
                        .errors(null)
                        .path(httpRequest.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //LOGIN
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(
            @RequestBody LoginRequestDTO request,
            HttpServletResponse response,
            HttpServletRequest httpRequest
    ) {

        AuthResponseDTO auth = service.login(request);

        CookieUtil.addAccessToken(response, auth.getAccessToken());
        CookieUtil.addRefreshToken(response, auth.getRefreshToken());

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Login successful")
                        .statusCode(200)
                        .data(null)
                        .errors(null)
                        .path(httpRequest.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //REFRESH TOKEN
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Object>> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        String refreshToken = null;

        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        AuthResponseDTO auth = service.refresh(refreshToken);

        CookieUtil.addAccessToken(response, auth.getAccessToken());
        CookieUtil.addRefreshToken(response, auth.getRefreshToken());

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Token refreshed successfully")
                        .statusCode(200)
                        .data(null)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            jwtService.blacklistToken(token);
        }

        CookieUtil.clearCookies(response);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Logged out successfully")
                        .statusCode(200)
                        .data(null)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //FORGOT PASSWORD
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Object>> forgotPassword(
            @RequestBody ForgotPasswordRequestDTO request,
            HttpServletRequest httpRequest
    ) {

        verificationService.forgotPassword(request);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Password changed successfully")
                        .statusCode(200)
                        .data(null)
                        .errors(null)
                        .path(httpRequest.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //CHANGE PASSWORD
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Object>> changePassword(
            @RequestBody ChangePasswordRequestDTO request,
            HttpServletRequest httpRequest
    ) {

        userService.changePassword(request);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Password updated successfully")
                        .statusCode(200)
                        .data(null)
                        .errors(null)
                        .path(httpRequest.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}