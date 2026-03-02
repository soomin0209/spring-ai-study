package com.study.springai.dto;

import java.util.List;

public record SentimentResult(
    String sentiment,
    double confidence,
    String explanation,
    List<String> keywords
) {}
