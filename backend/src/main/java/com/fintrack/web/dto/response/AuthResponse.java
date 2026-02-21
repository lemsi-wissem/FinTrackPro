package com.fintrack.web.dto.response;

public record AuthResponse(String accessToken, String tokenType) {
    public AuthResponse(String accessToken) {
        this(accessToken, "Bearer");
    }
}
