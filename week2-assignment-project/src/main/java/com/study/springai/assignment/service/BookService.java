package com.study.springai.assignment.service;

import com.study.springai.assignment.dto.BookAnalysisRequest;
import com.study.springai.assignment.dto.BookRecommendRequest;
import com.study.springai.assignment.dto.BookRecommendation;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BookService {
    private final ChatClient chatClient;

    public BookService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    // TODO 1: 도서 추천 기능 구현 (Ch2 - 텍스트 대화)
    // ChatClient의 Fluent API(prompt → user → call → content)를 사용하여
    // request의 genre, mood, count 정보를 포함한 프롬프트를 작성하고 응답을 반환하세요.
    public String recommendBooks(BookRecommendRequest request) {
        throw new UnsupportedOperationException("TODO 1: 도서 추천 기능을 구현하세요");
    }

    // TODO 2: 도서 분석 기능 구현 (Ch3 - 프롬프트 템플릿)
    // prompts/book-analysis.st 템플릿 파일을 ClassPathResource로 로드한 뒤
    // PromptTemplate을 사용하여 변수를 치환하고 ChatClient로 실행하세요.
    public String analyzeBook(BookAnalysisRequest request) {
        throw new UnsupportedOperationException("TODO 2: 도서 분석 기능을 구현하세요");
    }

    // TODO 3: 구조화된 도서 추천 기능 구현 (Ch4 - 구조화된 출력)
    // ChatClient의 entity() 메서드와 ParameterizedTypeReference를 사용하여
    // 응답을 List<BookRecommendation> 형태로 변환하여 반환하세요.
    public List<BookRecommendation> getStructuredRecommendations(BookRecommendRequest request) {
        throw new UnsupportedOperationException("TODO 3: 구조화된 도서 추천 기능을 구현하세요");
    }

    // TODO 4: 제로-샷 도서 분류 기능 구현 (프롬프트 엔지니어링)
    // 예시를 제공하지 않고, 명확한 지시문만으로 도서 설명의 장르를 분류하세요.
    // 분류 카테고리와 분류 기준을 프롬프트에 명시하는 것이 핵심입니다.
    public String classifyBookZeroShot(String bookDescription) {
        throw new UnsupportedOperationException("TODO 4: 제로-샷 도서 분류 기능을 구현하세요");
    }

    // TODO 5: 스텝-백 도서 분석 기능 구현 (프롬프트 엔지니어링)
    // 구체적 질문에 바로 답하지 않고, 먼저 상위 개념(배경, 원리)을 탐색한 뒤
    // 이를 바탕으로 답변을 도출하는 단계적 프롬프트를 설계하세요.
    public String analyzeWithStepBack(String title, String question) {
        throw new UnsupportedOperationException("TODO 5: 스텝-백 도서 분석 기능을 구현하세요");
    }
}
