package com.example.bloggenerator.service;

import com.example.bloggenerator.dto.BlogRequest;
import com.example.bloggenerator.dto.BlogResponse;

public interface AIService {
    BlogResponse generateBlog(BlogRequest request);
}