package com.study.springai.controller;

import com.study.springai.dto.ChatRequest;
import com.study.springai.dto.SentimentResult;
import com.study.springai.service.ChatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/simple")
    public Map<String, String> chat(@RequestBody ChatRequest request) {
        String response = chatService.chat(request.message());
        return Map.of("response", response);
    }

    @PostMapping("/system")
    public Map<String, String> chatWithSystem(@RequestBody ChatRequest request) {
        String response = chatService.chatWithSystem(request.message(), request.systemMessage());
        return Map.of("response", response);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestParam String message) {
        return chatService.streamChat(message)
            .map(token -> {
                try {
                    return objectMapper.writeValueAsString(token);
                } catch (JsonProcessingException e) {
                    return "\"" + token + "\"";
                }
            });
    }

    @PostMapping("/sentiment")
    public SentimentResult analyzeSentiment(@RequestBody Map<String, String> request) {
        return chatService.analyzeSentiment(request.get("text"));
    }
}
