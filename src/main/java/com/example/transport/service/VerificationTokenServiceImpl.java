package com.example.transport.service;

import com.example.transport.dto.ForgotPasswordRequestDTO;
import com.example.transport.dto.SendCodeRequestDTO;
import com.example.transport.dto.VerifyCodeRequestDTO;
import com.example.transport.exception.BadRequestException;
import com.example.transport.exception.ResourceNotFoundException;
import com.example.transport.model.User;
import com.example.transport.model.VerificationToken;
import com.example.transport.repository.UserRepository;
import com.example.transport.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Transactional
@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService{

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RedisService redisService;

    public User getUserByEmailOrPhone(String email, String phone) {

        if (email != null) {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email"));
        }

        if (phone != null) {
            return userRepository.findByPhoneNo(phone)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with phone"));
        }

        throw new BadRequestException("Email or phone must be provided");
    }

    @Override
    public String generateCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    @Override
    public void sendCode(SendCodeRequestDTO request) {

        String email = request.getEmail();
        String phone = request.getPhone();

        getUserByEmailOrPhone(email, phone);

        Optional<VerificationToken> existingToken =
                tokenRepository.findLatestByEmailOrPhone(email, phone);

        if (existingToken.isPresent()) {

            VerificationToken token = existingToken.get();

            //RATE LIMIT: 1 request per 60 seconds
            if (token.getLastSentAt() != null &&
                    token.getLastSentAt().plusSeconds(60).isAfter(LocalDateTime.now())) {

                throw new BadRequestException("Please wait before requesting another code");
            }
        }

        String code = generateCode();

        VerificationToken token = new VerificationToken();
        token.setCode(code);
        token.setEmail(email);
        token.setPhone(phone);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        token.setUsed(false);
        token.setAttempts(0);
        token.setLastSentAt(LocalDateTime.now());

        if (email != null) {
            tokenRepository.deleteAllByEmail(email);
        }

        if (phone != null) {
            tokenRepository.deleteAllByPhone(phone);
        }

        String key = "verify:" + (email != null ? email : phone);

        redisService.saveCode(key, code, 10);

        if (request.getEmail() != null) {
            emailService.sendVerificationEmail(request.getEmail(), code);
        }

        if (phone != null) {
            System.out.println("SMS CODE: " + code); // temporarily
        }
    }

    @Override
    public void verifyCode(VerifyCodeRequestDTO request) {

        String key = "verify:" + (request.getEmail() != null ? request.getEmail() : request.getPhone());

        String storedCode = redisService.getCode(key);

        if (request.getEmail() == null && request.getPhone() == null) {
            throw new BadRequestException("Email or Phone must be provided!!");
        }

        if (storedCode == null) {
            throw new BadRequestException("Code expired");
        }

        if (!storedCode.equals(request.getCode())) {
            throw new BadRequestException("Invalid code");
        }

        redisService.deleteCode(key);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequestDTO request) {

        verifyCode(new VerifyCodeRequestDTO(
                request.getCode(),
                request.getEmail(),
                request.getPhone()
        ));

        User user = getUserByEmailOrPhone(
                request.getEmail(),
                request.getPhone()
        );

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
