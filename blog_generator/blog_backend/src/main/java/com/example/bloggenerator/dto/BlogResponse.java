package com.example.bloggenerator.dto;

public record BlogResponse(
    String title,
    String content,
    String status
) {}