// ============================================================
//  카카오톡 스타일 AI 채팅 — 프론트엔드 로직
// ============================================================

let isStreaming = false;  // 스트리밍 모드 여부
let isProcessing = false; // 응답 대기 중 여부

// ============================================================
//  초기화
// ============================================================
document.addEventListener('DOMContentLoaded', () => {
    // 오늘 날짜 표시
    const today = new Date();
    const dateStr = `${today.getFullYear()}년 ${today.getMonth() + 1}월 ${today.getDate()}일 ${getDayName(today.getDay())}`;
    document.getElementById('todayDate').textContent = dateStr;

    // 입력 필드 포커스
    document.getElementById('messageInput').focus();

    // 입력 감지 → 전송 버튼 활성화
    document.getElementById('messageInput').addEventListener('input', function () {
        document.getElementById('sendBtn').disabled = this.value.trim() === '';
    });

    // marked.js 설정
    if (typeof marked !== 'undefined') {
        marked.setOptions({
            breaks: true,
            gfm: true,
            highlight: function (code, lang) {
                if (typeof hljs !== 'undefined' && lang && hljs.getLanguage(lang)) {
                    return hljs.highlight(code, { language: lang }).value;
                }
                return code;
            }
        });
    }
});

function getDayName(day) {
    return ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'][day];
}

// ============================================================
//  메시지 전송
// ============================================================
function sendMessage() {
    const input = document.getElementById('messageInput');
    const message = input.value.trim();
    if (!message || isProcessing) return;

    // 내 메시지 추가
    appendMessage(message, 'sent');

    // 입력창 초기화
    input.value = '';
    input.style.height = 'auto';
    document.getElementById('sendBtn').disabled = true;

    // 응답 대기
    isProcessing = true;

    if (isStreaming) {
        handleStreamChat(message);
    } else {
        handleChat(message);
    }
}

// ============================================================
//  일반 채팅 (POST /api/chat)
// ============================================================
async function handleChat(message) {
    // 타이핑 인디케이터 표시
    const typingEl = showTypingIndicator();

    try {
        const response = await fetch('/api/chat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ message: message })
        });

        const data = await response.json();
        removeTypingIndicator(typingEl);

        if (data.success) {
            appendMessage(data.message, 'received', true);
        } else {
            appendMessage(data.error || '응답을 받지 못했습니다.', 'received', false, true);
        }
    } catch (error) {
        removeTypingIndicator(typingEl);
        appendMessage('서버 연결에 실패했습니다: ' + error.message, 'received', false, true);
    } finally {
        isProcessing = false;
        document.getElementById('messageInput').focus();
    }
}

// ============================================================
//  스트리밍 채팅 (POST /api/chat/stream → SSE)
// ============================================================
async function handleStreamChat(message) {
    // 타이핑 인디케이터 표시 (응답 대기 중)
    const typingEl = showTypingIndicator();

    let row, bubble;
    let fullText = '';

    try {
        const response = await fetch('/api/chat/stream', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ message: message })
        });

        const reader = response.body.getReader();
        const decoder = new TextDecoder();

        while (true) {
            const { done, value } = await reader.read();
            if (done) break;

            const chunk = decoder.decode(value, { stream: true });

            // SSE 포맷 파싱: "data:토큰\n\n"
            const lines = chunk.split('\n');
            for (const line of lines) {
                if (line.startsWith('data:')) {
                    const token = line.substring(5);
                    if (token) {
                        // 첫 토큰 도착 시 타이핑 인디케이터 제거 & 말풍선 생성
                        if (!bubble) {
                            removeTypingIndicator(typingEl);
                            ({ row, bubble } = createEmptyReceivedMessage());
                            bubble.classList.add('markdown-body');
                        }
                        fullText += token;
                        bubble.innerHTML = renderMarkdown(fullText);
                    }
                }
            }

            scrollToBottom();
        }

        // 코드 하이라이팅 적용
        bubble.querySelectorAll('pre code').forEach(block => {
            if (typeof hljs !== 'undefined') hljs.highlightElement(block);
        });

    } catch (error) {
        removeTypingIndicator(typingEl);
        if (!bubble) {
            ({ row, bubble } = createEmptyReceivedMessage());
        }
        if (!fullText) {
            bubble.textContent = '스트리밍 연결 실패: ' + error.message;
            bubble.classList.add('error-bubble');
        }
    } finally {
        isProcessing = false;
        document.getElementById('messageInput').focus();
    }
}

// ============================================================
//  메시지 DOM 생성
// ============================================================
function appendMessage(text, type, isMarkdown = false, isError = false) {
    const chatBody = document.getElementById('chatBody');
    const row = document.createElement('div');
    row.className = `message-row ${type}`;

    const now = new Date();
    const timeStr = `${now.getHours() < 10 ? '0' : ''}${now.getHours()}:${now.getMinutes() < 10 ? '0' : ''}${now.getMinutes()}`;

    if (type === 'sent') {
        // 보낸 메시지 (오른쪽)
        row.innerHTML = `
            <div class="bubble-time-row">
                <div class="message-bubble">${escapeHtml(text)}</div>
                <span class="message-time">${timeStr}</span>
            </div>
        `;
    } else {
        // 받은 메시지 (왼쪽) — AI
        const bubbleContent = isMarkdown ? renderMarkdown(text) : escapeHtml(text);
        const errorClass = isError ? ' error-bubble' : '';
        const markdownClass = isMarkdown ? ' markdown-body' : '';

        row.innerHTML = `
            <div class="ai-profile">
                <svg viewBox="0 0 24 24" fill="none">
                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2z" fill="currentColor" opacity="0.2"/>
                    <path d="M12 6a3.5 3.5 0 100 7 3.5 3.5 0 000-7zM7 17.5c0-2.5 2.24-4 5-4s5 1.5 5 4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                </svg>
            </div>
            <div class="message-content-wrapper">
                <span class="ai-name">AI 어시스턴트</span>
                <div class="bubble-time-row">
                    <div class="message-bubble${markdownClass}${errorClass}">${bubbleContent}</div>
                    <span class="message-time">${timeStr}</span>
                </div>
            </div>
        `;

        // 코드 하이라이팅
        if (isMarkdown) {
            row.querySelectorAll('pre code').forEach(block => {
                if (typeof hljs !== 'undefined') hljs.highlightElement(block);
            });
        }
    }

    chatBody.appendChild(row);
    scrollToBottom();
}

// 스트리밍용 빈 AI 메시지 생성
function createEmptyReceivedMessage() {
    const chatBody = document.getElementById('chatBody');
    const row = document.createElement('div');
    row.className = 'message-row received';

    const now = new Date();
    const timeStr = `${now.getHours() < 10 ? '0' : ''}${now.getHours()}:${now.getMinutes() < 10 ? '0' : ''}${now.getMinutes()}`;

    row.innerHTML = `
        <div class="ai-profile">
            <svg viewBox="0 0 24 24" fill="none">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2z" fill="currentColor" opacity="0.2"/>
                <path d="M12 6a3.5 3.5 0 100 7 3.5 3.5 0 000-7zM7 17.5c0-2.5 2.24-4 5-4s5 1.5 5 4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
        </div>
        <div class="message-content-wrapper">
            <span class="ai-name">AI 어시스턴트</span>
            <div class="bubble-time-row">
                <div class="message-bubble markdown-body"></div>
                <span class="message-time">${timeStr}</span>
            </div>
        </div>
    `;

    chatBody.appendChild(row);
    const bubble = row.querySelector('.message-bubble');
    return { row, bubble };
}

// ============================================================
//  타이핑 인디케이터
// ============================================================
function showTypingIndicator() {
    const chatBody = document.getElementById('chatBody');
    const row = document.createElement('div');
    row.className = 'message-row received typing-row';

    row.innerHTML = `
        <div class="ai-profile">
            <svg viewBox="0 0 24 24" fill="none">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2z" fill="currentColor" opacity="0.2"/>
                <path d="M12 6a3.5 3.5 0 100 7 3.5 3.5 0 000-7zM7 17.5c0-2.5 2.24-4 5-4s5 1.5 5 4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
        </div>
        <div class="message-content-wrapper">
            <span class="ai-name">AI 어시스턴트</span>
            <div class="bubble-time-row">
                <div class="message-bubble">
                    <div class="typing-indicator">
                        <span></span><span></span><span></span>
                    </div>
                </div>
            </div>
        </div>
    `;

    chatBody.appendChild(row);
    scrollToBottom();
    return row;
}

function removeTypingIndicator(el) {
    if (el && el.parentNode) {
        el.parentNode.removeChild(el);
    }
}

// ============================================================
//  유틸리티
// ============================================================
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function renderMarkdown(text) {
    if (typeof marked !== 'undefined' && text) {
        return marked.parse(text);
    }
    return escapeHtml(text);
}

function scrollToBottom() {
    const chatBody = document.getElementById('chatBody');
    chatBody.scrollTop = chatBody.scrollHeight;
}

// 텍스트 영역 자동 높이 조절
function autoResize(textarea) {
    textarea.style.height = 'auto';
    textarea.style.height = Math.min(textarea.scrollHeight, 120) + 'px';
}

// Enter → 전송, Shift+Enter → 줄바꿈
// 한글 IME 조합 중(isComposing)에는 무시하여 마지막 글자가 남는 버그 방지
function handleKeyDown(event) {
    if (event.key === 'Enter' && !event.shiftKey && !event.isComposing) {
        event.preventDefault();
        sendMessage();
    }
}

// ============================================================
//  스트리밍 모드 토글
// ============================================================
function toggleStreamMode() {
    isStreaming = !isStreaming;
    const indicator = document.getElementById('streamIndicator');
    const btn = document.getElementById('streamToggle');

    if (isStreaming) {
        indicator.classList.add('active');
        btn.classList.add('stream-active');
    } else {
        indicator.classList.remove('active');
        btn.classList.remove('stream-active');
    }
}

// ============================================================
//  채팅 초기화
// ============================================================
function clearChat() {
    const chatBody = document.getElementById('chatBody');
    // 날짜 구분선과 시스템 메시지만 남기기
    const children = Array.from(chatBody.children);
    children.forEach(child => {
        if (!child.classList.contains('date-divider') && !child.classList.contains('system-message')) {
            chatBody.removeChild(child);
        }
    });
}
