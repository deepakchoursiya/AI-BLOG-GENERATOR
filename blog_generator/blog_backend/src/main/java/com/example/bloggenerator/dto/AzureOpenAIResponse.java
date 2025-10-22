package com.example.bloggenerator.dto;

import java.util.List;

public record AzureOpenAIResponse(
    List<Choice> choices
) {
    public record Choice(Message message) {}
    public record Message(String content) {}
}