package com.study.springai.dto;

public record ChatResponse(String content, String model, long tokenUsage) {}
