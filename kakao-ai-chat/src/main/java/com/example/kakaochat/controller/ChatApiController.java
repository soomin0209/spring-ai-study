package com.example.kakaochat.controller;

import com.example.kakaochat.model.ChatRequest;
import com.example.kakaochat.model.ChatResponse;
import com.example.kakaochat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatApiController {

    private final ChatService chatService;

    /**
     * 일반 채팅 — 전체 응답을 한 번에 반환
     */
    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return chatService.chat(request);
    }

    /**
     * 스트리밍 채팅 — SSE로 토큰 단위 실시간 전송
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestBody ChatRequest request) {
        return chatService.streamChat(request);
    }

    /**
     * 헬스체크
     */
    @GetMapping("/health")
    public ChatResponse health() {
        return ChatResponse.success("OK");
    }
}
