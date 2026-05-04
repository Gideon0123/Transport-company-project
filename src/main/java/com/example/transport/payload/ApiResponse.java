package com.example.transport.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private int statusCode;
    private T data;
    private List<String> errors;
    private String path;
    private String traceId;
    private LocalDateTime timestamp;


}