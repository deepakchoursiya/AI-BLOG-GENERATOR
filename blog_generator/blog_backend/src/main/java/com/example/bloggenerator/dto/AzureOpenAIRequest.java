package com.example.bloggenerator.dto;

import java.util.List;

public record AzureOpenAIRequest(
    List<Message> messages,
    int max_tokens,
    double temperature
) {
    public record Message(String role, String content) {}
}