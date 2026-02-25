package com.example.kakaochat.service;

import com.example.kakaochat.model.ChatRequest;
import com.example.kakaochat.model.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * ============================================================
 *  채팅 서비스 — 프롬프트 기법 실습 가이드
 * ============================================================
 *
 *  이 파일의 chat() 메서드를 수정하면서 다양한 프롬프트 기법을 실험하세요.
 *  각 기법별 예시 코드가 주석으로 포함되어 있습니다.
 *
 *  수정 후 애플리케이션을 재시작하면 변경사항이 반영됩니다.
 *  (DevTools가 있으므로 자동 재시작될 수도 있습니다)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;

    // ================================================================
    //  기본 채팅 — 이 메서드를 수정해가면서 테스트 진행해보세요.
    // ================================================================
    public ChatResponse chat(ChatRequest request) {
        try {
            log.info("Chat request: {}", request.getMessage());

            /*
            String response = chatClient.prompt()
                    .user(request.getMessage())
                    .call()
                    .content();

             */

            // ──────────────────────────────────────────────
            //  [예시] 시스템 프롬프트 오버라이드
            //  → AiConfig의 defaultSystem 대신 여기서 직접 지정
            // ──────────────────────────────────────────────

            String response = chatClient.prompt()
                    .system("""
                            당신은 10대 인터넷 커뮤니티 말투로 말하는 AI입니다.
                                    모든 답변을 반말 + 자연스러운 줄임말을 사용해서 작성하세요.
                                    정확한 정보는 유지하세요.
                            """)
                    .user(request.getMessage())
                    .call()
                    .content();



            log.info("Chat response length: {}", response != null ? response.length() : 0);
            return ChatResponse.success(response);

        } catch (Exception e) {
            log.error("Chat error: ", e);
            return ChatResponse.error("AI 응답 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // ================================================================
    //  스트리밍 채팅 — 실시간 토큰 전송 (SSE)
    //  이 메서드를 수정해가면서 테스트 진행해보세요.
    // ================================================================
    public Flux<String> streamChat(ChatRequest request) {
        log.info("Stream chat request: {}", request.getMessage());

        return chatClient.prompt()
                .user(request.getMessage())
                .stream()
                .content();
    }
}
