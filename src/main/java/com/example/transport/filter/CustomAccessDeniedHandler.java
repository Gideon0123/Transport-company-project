package com.example.transport.filter;

import com.example.transport.payload.ApiResponse;
import com.example.transport.util.TraceIdUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException ex
    ) throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ApiResponse<Object> body = ApiResponse.builder()
                .success(false)
                .message("Access denied")
                .statusCode(403)
                .data(null)
                .errors(List.of(ex.getMessage()))
                .path(request.getRequestURI())
                .traceId(TraceIdUtil.generate())
                .timestamp(LocalDateTime.now())
                .build();

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}