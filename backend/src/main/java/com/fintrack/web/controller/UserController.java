package com.fintrack.web.controller;

import com.fintrack.domain.model.User;
import com.fintrack.domain.port.in.ChangePasswordUseCase;
import com.fintrack.domain.port.in.GetCurrentUserUseCase;
import com.fintrack.domain.port.in.UpdateProfileUseCase;
import com.fintrack.web.dto.request.ChangePasswordRequest;
import com.fintrack.web.dto.request.UpdateProfileRequest;
import com.fintrack.web.dto.response.UserResponse;
import com.fintrack.web.mapper.UserWebMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile endpoints")
public class UserController {

    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final UserWebMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        User user = getCurrentUserUseCase.getCurrentUser(userId);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        var command = new UpdateProfileUseCase.UpdateProfileCommand(userId, request.firstName(), request.lastName());
        User updated = updateProfileUseCase.updateProfile(command);
        return ResponseEntity.ok(userMapper.toResponse(updated));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        var command = new ChangePasswordUseCase.ChangePasswordCommand(
                userId, request.currentPassword(), request.newPassword());
        changePasswordUseCase.changePassword(command);
        return ResponseEntity.noContent().build();
    }
}
