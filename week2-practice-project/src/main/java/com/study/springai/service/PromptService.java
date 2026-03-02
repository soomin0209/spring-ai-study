package com.study.springai.service;

import com.study.springai.dto.CodeReviewRequest;
import com.study.springai.dto.CodeReviewResult;
import com.study.springai.dto.ProductDescription;
import com.study.springai.dto.ProductRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PromptService {
    private final ChatClient chatClient;

    public PromptService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    // 1. PromptTemplate으로 상품 설명 생성 (Ch3)
    public String generateProductDescription(ProductRequest request) {
        String template = """
            당신은 마케팅 전문가입니다.
            다음 상품에 대한 매력적인 설명을 작성해주세요.

            상품명: {productName}
            대상 고객: {targetAudience}
            톤앤매너: {tone}

            다음 형식으로 작성해주세요:
            1. 헤드라인 (한 줄)
            2. 상품 설명 (2-3문장)
            3. 핵심 특징 3가지
            4. CTA (Call to Action)
            """;

        PromptTemplate promptTemplate = new PromptTemplate(template);
        Prompt prompt = promptTemplate.create(Map.of(
            "productName", request.productName(),
            "targetAudience", request.targetAudience(),
            "tone", request.tone()
        ));

        return chatClient.prompt(prompt).call().content();
    }

    // 2. 구조화된 상품 설명 (Ch4 - entity)
    public ProductDescription generateStructuredProduct(ProductRequest request) {
        String userMessage = String.format(
            "상품명: %s, 대상고객: %s, 톤: %s에 맞는 마케팅 설명을 다음 JSON 형식으로 작성해주세요: {\"headline\": \"\", \"description\": \"\", \"keyFeatures\": [], \"callToAction\": \"\", \"rating\": 0}",
            request.productName(), request.targetAudience(), request.tone()
        );
        return chatClient.prompt()
            .user(userMessage)
            .call()
            .entity(ProductDescription.class);
    }

    // 3. 코드 리뷰 (Few-shot + 구조화된 출력 + 외부 프롬프트 템플릿)
    public CodeReviewResult reviewCode(CodeReviewRequest request) {
        // 외부 프롬프트 템플릿 파일 로드, Spring AI의 PromptTemplate이 내부적으로 StringTemplate(ST) 엔진을 사용
        ClassPathResource resource = new ClassPathResource("prompts/code-review.st");
        PromptTemplate template = new PromptTemplate(resource);

        // 템플릿 변수 치환
        String prompt = template.create(Map.of(
            "language", request.language(),
            "code", request.code()
        )).getContents();

        return chatClient.prompt()
            .user(prompt)
            .call()
            .entity(CodeReviewResult.class);
    }

    // 4. Few-shot 번역
    public String translateWithFewShot(String text, String targetLang) {
        String fewShotPrompt = """
            다음은 번역 예시입니다:

            입력: "Hello, how are you?"
            출력: "안녕하세요, 어떠세요?"

            입력: "The weather is nice today."
            출력: "오늘 날씨가 좋네요."

            이제 다음 텍스트를 %s로 번역해주세요:
            입력: "%s"
            출력:
            """.formatted(targetLang, text);

        return chatClient.prompt()
            .user(fewShotPrompt)
            .call()
            .content();
    }

    // 5. Chain-of-Thought 수학 문제
    public String solveWithCoT(String problem) {
        String cotPrompt = """
            다음 문제를 단계별로 풀어주세요.
            각 단계를 명확하게 설명하고, 최종 답을 제시해주세요.

            문제: %s

            풀이:
            """.formatted(problem);

        return chatClient.prompt()
            .user(cotPrompt)
            .call()
            .content();
    }

    // 6. Zero-shot 프롬프트 - 예시 없이 명확한 지시만으로 작업 수행
    public String classifyWithZeroShot(String text, String categories) {
        String zeroShotPrompt = """
            당신은 텍스트 분류 전문가입니다.

            다음 텍스트를 아래 카테고리 중 하나로 분류하세요.
            예시는 제공되지 않습니다. 텍스트의 내용을 분석하여 가장 적합한 카테고리를 선택하세요.

            카테고리: %s

            텍스트: "%s"

            다음 형식으로 응답하세요:
            - 분류 결과: [선택한 카테고리]
            - 신뢰도: [높음/중간/낮음]
            - 분류 근거: [왜 이 카테고리로 분류했는지 2-3문장으로 설명]
            """.formatted(categories, text);

        return chatClient.prompt()
            .user(zeroShotPrompt)
            .call()
            .content();
    }

    // 7. Step-back 프롬프트 - 구체적 질문 전에 상위 개념을 먼저 탐색
    public String solveWithStepBack(String question) {
        // Step 1: 상위 개념/원리를 먼저 질문
        String stepBackPrompt = """
            다음 질문에 답하기 전에, 먼저 관련된 상위 개념과 원리를 설명해주세요.

            원래 질문: "%s"

            다음 단계로 답변해주세요:

            ## 1단계: 상위 개념 탐색 (Step-Back)
            이 질문과 관련된 핵심 개념, 원리, 배경 지식을 먼저 정리해주세요.

            ## 2단계: 상위 개념 적용
            위에서 정리한 개념과 원리를 바탕으로, 원래 질문에 대한 답변을 구성해주세요.

            ## 3단계: 최종 답변
            위의 분석을 종합하여 명확하고 정확한 최종 답변을 제시해주세요.
            """.formatted(question);

        return chatClient.prompt()
            .user(stepBackPrompt)
            .call()
            .content();
    }
}
