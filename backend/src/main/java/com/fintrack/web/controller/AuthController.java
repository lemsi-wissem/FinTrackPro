package com.fintrack.web.controller;

import com.fintrack.domain.port.in.*;
import com.fintrack.domain.port.in.LoginUserUseCase.AuthTokens;
import com.fintrack.domain.port.in.LoginUserUseCase.LoginCommand;
import com.fintrack.domain.port.in.RegisterUserUseCase.RegisterUserCommand;
import com.fintrack.web.dto.request.LoginRequest;
import com.fintrack.web.dto.request.RegisterRequest;
import com.fintrack.web.dto.response.AuthResponse;
import com.fintrack.web.dto.response.MessageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Registration, login, token refresh, logout")
public class AuthController {

    private final RegisterUserUseCase registerUseCase;
    private final LoginUserUseCase loginUseCase;
    private final VerifyEmailUseCase verifyEmailUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUserUseCase logoutUseCase;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        registerUseCase.register(new RegisterUserCommand(
                request.email(), request.password(), request.firstName(), request.lastName()
        ));
        return ResponseEntity.status(201)
                .body(new MessageResponse("Registration successful. Please check your email to verify your account."));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(@RequestParam String token) {
        verifyEmailUseCase.verify(token);
        return ResponseEntity.ok(new MessageResponse("Email verified successfully."));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletResponse response) {
        AuthTokens tokens = loginUseCase.login(new LoginCommand(request.email(), request.password()));
        addRefreshTokenCookie(response, tokens.refreshToken());
        return ResponseEntity.ok(new AuthResponse(tokens.accessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        AuthTokens tokens = refreshTokenUseCase.refresh(refreshToken);
        addRefreshTokenCookie(response, tokens.refreshToken());
        return ResponseEntity.ok(new AuthResponse(tokens.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletRequest request,
            HttpServletResponse response) {
        String accessToken = extractAccessToken(request);
        if (refreshToken != null) {
            logoutUseCase.logout(refreshToken, accessToken);
        }
        clearRefreshTokenCookie(response);
        return ResponseEntity.ok(new MessageResponse("Logged out successfully."));
    }

    private String extractAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(false)
                .path("/api/v1/auth")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/api/v1/auth")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
