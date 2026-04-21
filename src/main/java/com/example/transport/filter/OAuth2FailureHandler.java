package com.example.transport.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(@NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException {

        String errorMessage = URLEncoder.encode(
                exception.getMessage(),
                StandardCharsets.UTF_8
        );

        getRedirectStrategy().sendRedirect(
                request,
                response,
                "http://localhost:3000/oauth-error?message=" + errorMessage
        );
    }
}