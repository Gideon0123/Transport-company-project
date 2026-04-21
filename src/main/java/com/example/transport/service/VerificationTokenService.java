package com.example.transport.service;

import com.example.transport.dto.ForgotPasswordRequestDTO;
import com.example.transport.dto.SendCodeRequestDTO;
import com.example.transport.dto.VerifyCodeRequestDTO;

public interface VerificationTokenService {

    public String generateCode();

    public void sendCode(SendCodeRequestDTO request);

    public void verifyCode(VerifyCodeRequestDTO request);

    void forgotPassword(ForgotPasswordRequestDTO request);
}
