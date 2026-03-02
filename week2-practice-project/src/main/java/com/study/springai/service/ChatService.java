package com.study.springai.service;

import com.study.springai.dto.SentimentResult;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatService {
    private final ChatClient chatClient;

    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    // 1. 기본 채팅
    public String chat(String message) {
        return chatClient.prompt()
            .user(message)
            .call()
            .content();
    }

    // 2. 시스템 메시지 포함 채팅
    public String chatWithSystem(String userMessage, String systemMessage) {
        return chatClient.prompt()
            .system(systemMessage)
            .user(userMessage)
            .call()
            .content();
    }

    // 3. 스트리밍 응답 (Flux<String>)
    public Flux<String> streamChat(String message) {
        return chatClient.prompt()
            .user(message)
            .stream()
            .content();
    }

    // 4. 감정 분석 (구조화된 출력 - Ch4)
    public SentimentResult analyzeSentiment(String text) {
        return chatClient.prompt()
            .user("다음 텍스트의 감정을 분석해주세요. JSON 형식으로 sentiment(긍정/부정/중립), confidence(0~1), explanation, keywords를 포함해주세요: " + text)
            .call()
            .entity(SentimentResult.class);
    }
}
