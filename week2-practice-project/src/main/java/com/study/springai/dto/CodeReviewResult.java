package com.study.springai.dto;

import java.util.List;

public record CodeReviewResult(
    int score,
    String summary,
    List<String> issues,
    List<String> suggestions,
    String refactoredCode
) {}
