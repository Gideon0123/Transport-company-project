package com.example.transport.service;

import com.example.transport.model.RefreshToken;
import com.example.transport.model.User;

public interface RefreshTokenService {

    RefreshToken create(User user);
    RefreshToken verify(String token);
    void revoke(RefreshToken token);
    void delete(RefreshToken token);
}
