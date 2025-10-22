package com.example.bloggenerator.service;

import com.example.bloggenerator.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AIServiceImpl implements AIService {
    
    private static final Logger logger = LoggerFactory.getLogger(AIServiceImpl.class);
    private final RestTemplate restTemplate;
    
    @Value("${spring.ai.azure.openai.api-key}")
    private String apiKey;
    
    @Value("${spring.ai.azure.openai.endpoint}")
    private String endpoint;
    
    @Value("${spring.ai.azure.openai.chat.options.deployment-name}")
    private String deploymentName;
    
    public AIServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Override
    public BlogResponse generateBlog(BlogRequest request) {
        logger.info("Generating blog for topic: {}", request.topic());
        
        try {
            String prompt = buildPrompt(request);
            String url = endpoint + "openai/deployments/" + deploymentName + "/chat/completions?api-version=2024-02-15-preview";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", apiKey);
            headers.set("Content-Type", "application/json");
            
            AzureOpenAIRequest azureRequest = new AzureOpenAIRequest(
                List.of(new AzureOpenAIRequest.Message("user", prompt)),
                request.wordCount() != null ? request.wordCount() * 2 : 1000,
                0.7
            );
            
            HttpEntity<AzureOpenAIRequest> entity = new HttpEntity<>(azureRequest, headers);
            AzureOpenAIResponse response = restTemplate.exchange(url, HttpMethod.POST, entity, AzureOpenAIResponse.class).getBody();
            
            if (response == null || response.choices().isEmpty()) {
                logger.error("Received null or empty response from Azure OpenAI");
                return new BlogResponse("Error", "No response from AI service", "error");
            }
            
            String content = response.choices().get(0).message().content();
            if (content == null || content.trim().isEmpty()) {
                logger.error("Received empty content from Azure OpenAI");
                return new BlogResponse("Error", "Empty response from AI service", "error");
            }
            
            BlogResponse result = parseContent(content);
            logger.info("Successfully generated blog with title: {}", result.title());
            return result;
            
        } catch (Exception e) {
            logger.error("Failed to generate blog for topic: {}", request.topic(), e);
            return new BlogResponse("Error", "Failed to generate blog: " + e.getMessage(), "error");
        }
    }
    
    private BlogResponse parseContent(String content) {
        String[] parts = content.split("\n\n", 2);
        String title = parts.length > 1 ? parts[0].replace("Title: ", "").trim() : "Generated Blog";
        String blogContent = parts.length > 1 ? parts[1].trim() : content.trim();
        return new BlogResponse(title, blogContent, "success");
    }
    
    private String buildPrompt(BlogRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Write a blog post about: ").append(request.topic());
        
        if (request.tone() != null) {
            prompt.append("\nTone: ").append(request.tone());
        }
        
        if (request.wordCount() != null) {
            prompt.append("\nTarget word count: ").append(request.wordCount());
        }
        
        prompt.append("\n\nFormat the response with 'Title: [title]' on the first line, followed by a blank line, then the blog content.");
        
        return prompt.toString();
    }
}