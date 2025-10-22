package com.example.bloggenerator.controller;

import com.example.bloggenerator.dto.BlogRequest;
import com.example.bloggenerator.dto.BlogResponse;
import com.example.bloggenerator.service.AIService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blog")
@CrossOrigin(origins = "*")
public class BlogController {

    private final AIService aiService;

    public BlogController(AIService aiService) {
        this.aiService = aiService;
    }
    
    @PostMapping("/generate")
    public ResponseEntity<BlogResponse> generateBlog(@Valid @RequestBody BlogRequest request) {
        BlogResponse response = aiService.generateBlog(request);
        return ResponseEntity.ok(response);
    }
}