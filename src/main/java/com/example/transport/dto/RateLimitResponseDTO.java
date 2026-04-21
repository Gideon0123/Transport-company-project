package com.example.transport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RateLimitResponseDTO {

    private boolean allowed;
    private long remaining;
    private long limit;
    private long resetTime;

//    public RateLimitResponse(boolean allowed, long remaining, long limit, long resetTime) {
//        this.allowed = allowed;
//        this.remaining = remaining;
//        this.limit = limit;
//        this.resetTime = resetTime;
//    }
}