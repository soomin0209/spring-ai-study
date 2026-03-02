package com.study.springai.dto;

import java.util.List;

public record ProductDescription(
    String headline,
    String description,
    List<String> keyFeatures,
    String callToAction,
    int rating
) {}
