package com.example.transport.service;

import com.example.transport.model.CustomerTrip;
import com.example.transport.model.User;

public interface EmailService {

    void sendVerificationEmail(String to, String code);
}
