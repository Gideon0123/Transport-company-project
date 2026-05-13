package com.example.transport.service;

import com.example.transport.dto.events.VerificationEmailEvent;
import com.example.transport.rabbitmq.VerificationEmailProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Profile("!test")
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final VerificationEmailProducer verificationEmailProducer;

    @Override
    public void sendVerificationEmail(String to, String code) {

        VerificationEmailEvent event =
                VerificationEmailEvent.builder()
                        .to(to)
                        .code(code)
                        .build();

        verificationEmailProducer.sendVerificationEmail(event);
    }


}