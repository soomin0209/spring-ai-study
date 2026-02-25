package com.example.kakaochat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 로깅을 끄려면 이 클래스의 @Configuration을 주석
 */
@Slf4j
@Configuration
public class HttpLoggingConfig {

    @Bean
    RestClientCustomizer loggingRestClientCustomizer() {
        return restClientBuilder -> restClientBuilder
                .requestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .requestInterceptor(new AiHttpLoggingInterceptor());
    }

    /**
     * HTTP 요청/응답 본문을 로그로 출력하는 인터셉터
     */
    static class AiHttpLoggingInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {

            // ── 요청 로깅 ──
            String requestBody = new String(body, StandardCharsets.UTF_8);
            log.info("""

                    >>>>>> AI REQUEST >>>>>>
                    {} {}
                    Content-Type: {}
                    Body:
                    {}
                    """,
                    request.getMethod(),
                    request.getURI(),
                    request.getHeaders().getContentType(),
                    prettyTruncate(requestBody, 3000)
            );

            // ── 실행 ──
            ClientHttpResponse response = execution.execute(request, body);

            // ── 응답 로깅 ──
            byte[] responseBody = response.getBody().readAllBytes();
            String responseStr = new String(responseBody, StandardCharsets.UTF_8);
            log.info("""

                    <<<<<< AI RESPONSE <<<<<<
                    Status: {}
                    Body:
                    {}
                    """,
                    response.getStatusCode(),
                    prettyTruncate(responseStr, 2000)
            );

            // 응답을 다시 읽을 수 있도록 BufferingClientHttpRequestFactory 사용
            return response;
        }

        /**
         * 너무 긴 본문은 잘라서 로그에 표시
         */
        private String prettyTruncate(String text, int maxLength) {
            if (text == null) return "(empty)";
            if (text.length() <= maxLength) return text;
            return text.substring(0, maxLength) + "\n... (truncated, total " + text.length() + " chars)";
        }
    }
}
