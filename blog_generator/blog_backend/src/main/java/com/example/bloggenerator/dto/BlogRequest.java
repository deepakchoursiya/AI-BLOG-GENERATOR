package com.example.bloggenerator.dto;

import jakarta.validation.constraints.NotBlank;

public record BlogRequest(
    @NotBlank(message = "Topic is required")
    String topic,
    
    String tone,
    Integer wordCount
) {}