package com.example.kakaochat.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * ============================================================
 *  AI 설정 파일 — 프롬프트 기법 실습의 핵심 수정 포인트!
 * ============================================================
 *
 *  이 파일에서 ChatClient를 구성할 때 다양한 프롬프트 기법을 적용할 수 있습니다.
 *
 *  예)
 *  시스템 프롬프트 변경
 *    → defaultSystem("...") 의 내용을 변경해보세요
 *    → 예: "당신은 요리 전문가입니다" / "You are a pirate" 등
 *
 *  Few-Shot 프롬프트
 *    → defaultSystem 안에 예시를 포함하세요
 *    → 예: "다음 형식으로 답변하세요:\n질문: ... → 답변: ..."
 *
 *  Chain of Thought
 *    → 시스템 프롬프트에 "단계별로 생각하세요" 추가
 *
 *  Output Format 지정
 *    → "반드시 JSON 형식으로 답변하세요" 등 추가
 */
@Configuration
public class AiConfig {

    // ================================================================
    //  기본 프로필: OpenAI 사용
    //  → openAiChatModel Bean이 자동 생성됨
    // ================================================================

    @Bean
    @Profile("!ollama")
    ChatClient chatClient(@Qualifier("openAiChatModel") ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                // 시스템 프롬프트를 수정해보세요
                .defaultSystem("""
                        당신은 친절하고 도움이 되는 AI 어시스턴트입니다.
                        사용자의 질문에 명확하고 간결하게 답변합니다.
                        한국어로 답변합니다.
                        """)
                // 여기를 변경하면 AI의 성격이 바뀝니다
                .build();
    }

    // ================================================================
    //  Ollama 프로필: 로컬 LLM 사용
    //  → 실행 시: ./gradlew bootRun --args='--spring.profiles.active=ollama'
    //  → ollamaChatModel Bean이 자동 생성됨
    // ================================================================

    @Bean
    @Profile("ollama")
    ChatClient ollamaChatClient(@Qualifier("ollamaChatModel") ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                        당신은 친절하고 도움이 되는 AI 어시스턴트입니다.
                        사용자의 질문에 명확하고 간결하게 답변합니다.
                        한국어로 답변합니다.
                        """)
                .build();
    }
}
