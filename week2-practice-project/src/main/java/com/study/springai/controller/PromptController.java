package com.study.springai.controller;

import com.study.springai.dto.CodeReviewRequest;
import com.study.springai.dto.CodeReviewResult;
import com.study.springai.dto.ProductDescription;
import com.study.springai.dto.ProductRequest;
import com.study.springai.service.PromptService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/prompt")
public class PromptController {
    private final PromptService promptService;

    public PromptController(PromptService promptService) {
        this.promptService = promptService;
    }

    @PostMapping("/product")
    public Map<String, String> generateProduct(@RequestBody ProductRequest request) {
        return Map.of("description", promptService.generateProductDescription(request));
    }

    @PostMapping("/product/structured")
    public ProductDescription generateStructuredProduct(@RequestBody ProductRequest request) {
        return promptService.generateStructuredProduct(request);
    }

    @PostMapping("/code-review")
    public CodeReviewResult reviewCode(@RequestBody CodeReviewRequest request) {
        return promptService.reviewCode(request);
    }

    @PostMapping("/translate")
    public Map<String, String> translate(@RequestBody Map<String, String> request) {
        return Map.of("translation", promptService.translateWithFewShot(request.get("text"), request.get("targetLang")));
    }

    @PostMapping("/solve")
    public Map<String, String> solve(@RequestBody Map<String, String> request) {
        return Map.of("solution", promptService.solveWithCoT(request.get("problem")));
    }

    @PostMapping("/zero-shot")
    public Map<String, String> classifyZeroShot(@RequestBody Map<String, String> request) {
        return Map.of("result", promptService.classifyWithZeroShot(request.get("text"), request.get("categories")));
    }

    @PostMapping("/step-back")
    public Map<String, String> solveStepBack(@RequestBody Map<String, String> request) {
        return Map.of("result", promptService.solveWithStepBack(request.get("question")));
    }
}
