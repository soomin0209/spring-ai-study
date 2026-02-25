package com.example.kakaochat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String message;
    private boolean success;
    private String error;

    public static ChatResponse success(String message) {
        return new ChatResponse(message, true, null);
    }

    public static ChatResponse error(String error) {
        return new ChatResponse(null, false, error);
    }
}
