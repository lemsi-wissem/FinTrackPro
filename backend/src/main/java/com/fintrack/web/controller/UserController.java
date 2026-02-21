package com.fintrack.web.controller;

import com.fintrack.domain.model.User;
import com.fintrack.domain.port.in.GetCurrentUserUseCase;
import com.fintrack.web.dto.response.UserResponse;
import com.fintrack.web.mapper.UserWebMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile endpoints")
public class UserController {

    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final UserWebMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        User user = getCurrentUserUseCase.getCurrentUser(userId);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }
}
