package com.example.transport.controller;

import com.example.transport.dto.SendCodeRequestDTO;
import com.example.transport.dto.VerifyCodeRequestDTO;
import com.example.transport.payload.ApiResponse;
import com.example.transport.service.VerificationTokenService;
import com.example.transport.util.TraceIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationTokenService verificationService;

    //SEND CODE
    @PostMapping("/send-code")
    public ResponseEntity<ApiResponse<Object>> sendCode(
            @RequestBody SendCodeRequestDTO request,
            HttpServletRequest httpRequest
    ) {
        verificationService.sendCode(request);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Code sent successfully")
                        .statusCode(200)
                        .data(null)
                        .errors(null)
                        .path(httpRequest.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    //VERIFY CODE
    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse<Object>> verifyCode(
            @RequestBody VerifyCodeRequestDTO request,
            HttpServletRequest httpRequest
    ) {
        verificationService.verifyCode(request);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Code verified successfully")
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