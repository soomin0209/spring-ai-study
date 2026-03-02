# AI 기반 도서 추천 시스템

Spring AI를 활용한 도서 추천 시스템을 구현하는 과제입니다.
이 과제를 통해 Ch2 텍스트 대화, Ch3 프롬프트 엔지니어링, Ch4 구조화된 출력을 실습합니다.

## 학습 목표

1. **Ch2 텍스트 대화**: ChatClient를 사용한 기본 AI 대화 구현
2. **Ch3 프롬프트 엔지니어링**: PromptTemplate을 사용한 구조화된 프롬프트 관리
3. **Ch4 구조화된 출력**: entity() 메서드를 사용한 JSON/객체 형태의 응답 획득
4. **프롬프트 기법**: 제로-샷, 스텝-백 프롬프트 기법 실습

## 프로젝트 구조

```
src/main/java/com/study/springai/assignment/
├── Week2AssignmentProjectApplication.java          # Spring Boot 메인 애플리케이션
├── config/
│   └── AiConfig.java                   # AI 모델 설정 (OpenAI/Ollama 전환)
├── controller/
│   └── BookController.java             # REST API 엔드포인트 (완성)
├── service/
│   └── BookService.java                # 비즈니스 로직 (TODO)
└── dto/
    ├── BookRecommendRequest.java       # 도서 추천 요청 DTO (완성)
    ├── BookAnalysisRequest.java        # 도서 분석 요청 DTO (완성)
    └── BookRecommendation.java         # 도서 추천 응답 DTO (완성)

src/main/resources/
├── application.yml                      # 애플리케이션 설정 (완성)
├── templates/
│   └── index.html                      # 웹 UI (완성)
└── prompts/
    └── book-analysis.st                # 프롬프트 템플릿 (완성)
```

## 과제 요구사항

### TODO 1: 도서 추천 (Ch2 텍스트 대화)
**위치**: `BookService.java` → `recommendBooks()`

- **목표**: ChatClient의 Fluent API를 사용하여 장르와 분위기에 맞는 도서를 추천
- **입력**: BookRecommendRequest (genre, mood, count)
- **출력**: 추천 도서 텍스트 (String)
- **핵심 학습**: `prompt()` → `user()` → `call()` → `content()` 체인 이해

### TODO 2: 도서 분석 (Ch3 프롬프트 템플릿)
**위치**: `BookService.java` → `analyzeBook()`

- **목표**: PromptTemplate과 외부 템플릿 파일(`prompts/book-analysis.st`)을 사용하여 도서 분석 수행
- **입력**: BookAnalysisRequest (title, author)
- **출력**: 분석 결과 텍스트 (String)
- **핵심 학습**: ClassPathResource로 템플릿 로드 → PromptTemplate으로 변수 치환 → ChatClient 실행

### TODO 3: 구조화된 도서 추천 (Ch4 구조화된 출력)
**위치**: `BookService.java` → `getStructuredRecommendations()`

- **목표**: ChatClient의 `entity()` 메서드를 사용하여 BookRecommendation 리스트를 구조화된 형태로 반환
- **입력**: BookRecommendRequest (genre, mood, count)
- **출력**: List<BookRecommendation> (JSON 형태로 파싱된 객체 리스트)
- **핵심 학습**: `ParameterizedTypeReference`를 사용한 제네릭 타입 변환

### TODO 4: 제로-샷 도서 분류 (프롬프트 엔지니어링)
**위치**: `BookService.java` → `classifyBookZeroShot()`

- **목표**: 제로-샷 프롬프트 기법을 사용하여 도서 설명으로부터 장르를 분류
- **입력**: 도서 설명 텍스트 (String)
- **출력**: 분류 결과 (String)
- **핵심 학습**: 예시 없이 명확한 지시문만으로 AI에게 작업을 요청하는 방법

### TODO 5: 스텝-백 도서 분석 (프롬프트 엔지니어링)
**위치**: `BookService.java` → `analyzeWithStepBack()`

- **목표**: 스텝-백 프롬프트 기법을 사용하여 도서에 대한 심층 분석 수행
- **입력**: 도서 제목, 질문 (String, String)
- **출력**: 분석 결과 (String)
- **핵심 학습**: 구체적 질문 전에 상위 개념을 먼저 탐색하는 단계적 분석 방법

## 참고 API

- `ChatClient.prompt().user(message).call().content()` — 텍스트 응답 획득
- `ChatClient.prompt().user(message).call().entity(TypeRef)` — 구조화된 응답 획득
- `new PromptTemplate(resource).create(Map.of(...)).getContents()` — 템플릿 변수 치환
- `new ClassPathResource("prompts/파일명.st")` — 클래스패스에서 리소스 로드

## 실행 방법

### 방법 1: OpenAI (기본)

```bash
# 1. API Key 설정
export OPENAI_API_KEY=sk-...

# 2. 빌드 및 실행
./gradlew build
./gradlew bootRun
```

### 방법 2: Ollama (로컬 LLM)

API Key 없이 로컬에서 실습할 수 있습니다.

```bash
# 1. Ollama 설치 (https://ollama.com)
# 2. 모델 다운로드
ollama pull llama3.2

# 3. Ollama 프로필로 실행
./gradlew bootRun --args='--spring.profiles.active=ollama'
```

### 웹 UI 접근
- URL: `http://localhost:8080`
- 5개의 탭에서 기능 테스트

## API 엔드포인트

### POST /api/books/recommend
도서 추천 (Ch2)
```json
Request:
{
  "genre": "판타지",
  "mood": "따뜻한",
  "count": 5
}

Response: (텍스트)
```

### POST /api/books/analyze
도서 분석 (Ch3)
```json
Request:
{
  "title": "들개",
  "author": "이외수"
}

Response: (텍스트)
```

### POST /api/books/structured
구조화된 도서 추천 (Ch4)
```json
Request:
{
  "genre": "SF",
  "mood": "신비로운",
  "count": 3
}

Response:
[
  {
    "title": "도서명",
    "author": "저자명",
    "genre": "장르",
    "summary": "요약",
    "rating": 5,
    "reason": "추천 이유"
  }
]
```

### POST /api/books/zero-shot
제로-샷 도서 분류
```json
Request:
{
  "bookDescription": "한 소년이 마법 학교에 입학하여..."
}

Response:
{
  "result": "분류 결과 텍스트"
}
```

### POST /api/books/step-back
스텝-백 도서 분석
```json
Request:
{
  "title": "총균쇠",
  "question": "이 책이 현대 사회에 미친 영향은 무엇인가요?"
}

Response:
{
  "result": "분석 결과 텍스트"
}
```

## 테스트 시나리오

### 시나리오 1: 도서 추천 (TODO 1)
1. 탭 1 "도서 추천" 선택
2. 장르, 분위기, 추천 도서 수 입력
3. 추천 결과 확인

### 시나리오 2: 도서 분석 (TODO 2)
1. 탭 2 "도서 분석" 선택
2. 도서명, 저자명 입력
3. 분석 결과 확인

### 시나리오 3: 구조화된 추천 (TODO 3)
1. 탭 3 "구조화된 추천" 선택
2. 장르, 분위기, 도서 수 입력
3. 구조화된 카드 형태로 결과 표시 확인

### 시나리오 4: 제로-샷 분류 (TODO 4)
1. 탭 4 "제로-샷 분류" 선택
2. 도서 설명 텍스트 입력
3. 장르 분류 결과 확인

### 시나리오 5: 스텝-백 분석 (TODO 5)
1. 탭 5 "스텝-백 분석" 선택
2. 도서 제목, 질문 입력
3. 단계별 심층 분석 결과 확인

## 검증 항목

- [ ] TODO 1: ChatClient를 사용한 기본 도서 추천 구현
- [ ] TODO 2: PromptTemplate을 사용한 도서 분석 구현
- [ ] TODO 3: entity()를 사용한 구조화된 출력 구현
- [ ] TODO 4: 제로-샷 프롬프트를 사용한 도서 분류 구현
- [ ] TODO 5: 스텝-백 프롬프트를 사용한 심층 분석 구현
- [ ] 모든 엔드포인트가 정상 작동
- [ ] 웹 UI에서 5개 탭 모두 테스트 완료
- [ ] API 응답이 정확한 형식으로 반환됨

## 관련 리소스

- Spring AI 공식 문서: https://docs.spring.io/spring-ai/
- ChatClient API: https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/chat/client/ChatClient.html
- PromptTemplate: https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/chat/prompt/PromptTemplate.html

## 제출 체크리스트

- [ ] TODO 1~5 모든 구현 완료
- [ ] 프로젝트 빌드 성공 (`./gradlew build`)
- [ ] 애플리케이션 정상 실행 (`./gradlew bootRun`)
- [ ] 웹 UI에서 5개 탭 모든 기능 테스트 완료
- [ ] API 응답이 예상대로 작동
