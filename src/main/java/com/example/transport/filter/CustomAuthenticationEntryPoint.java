package com.example.transport.filter;

import com.example.transport.payload.ApiResponse;
import com.example.transport.util.TraceIdUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException ex
    ) throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiResponse<Object> body = ApiResponse.builder()
                .success(false)
                .message("Unauthorized")
                .statusCode(401)
                .data(null)
                .errors(List.of(ex.getMessage()))
                .path(request.getRequestURI())
                .traceId(TraceIdUtil.generate())
                .timestamp(LocalDateTime.now())
                .build();

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}