package com.example.transport.dto;

import lombok.Data;

import java.util.Map;

@Data
public class SearchRequest {
    private String keyword;
    private Map<String, Object> filters;
}