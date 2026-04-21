package com.example.transport.exception;

import com.example.transport.payload.ApiResponse;
import com.example.transport.util.TraceIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildResponse(ex.getMessage(), 404, request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorizedRequest(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildResponse(ex.getMessage(), 401, request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        return buildResponse(ex.getMessage(), 400, request);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Object>> handleForbiddenRequest(
            ForbiddenException ex,
            HttpServletRequest request
    ) {
        return buildResponse(ex.getMessage(), 403, request);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleConflict(
            ConflictException ex,
            HttpServletRequest request
    ) {
        return buildResponse(ex.getMessage(), 409, request);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request
    ) {
        return buildResponse(ex.getMessage(), 401, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

        return ResponseEntity.status(400).body(
                ApiResponse.builder()
                        .success(false)
                        .message("Validation failed")
                        .statusCode(400)
                        .data(null)
                        .errors(errors)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        return buildResponse("Internal server error", 500, request);
    }

    private ResponseEntity<ApiResponse<Object>> buildResponse(
            String message,
            int status,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(status).body(
                ApiResponse.builder()
                        .success(false)
                        .message(message)
                        .statusCode(status)
                        .data(null)
                        .errors(List.of(message))
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}