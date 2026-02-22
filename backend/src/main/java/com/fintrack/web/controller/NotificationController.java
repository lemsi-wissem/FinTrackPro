package com.fintrack.web.controller;

import com.fintrack.domain.model.Notification;
import com.fintrack.domain.port.in.GetNotificationsUseCase;
import com.fintrack.domain.port.in.MarkNotificationsReadUseCase;
import com.fintrack.web.dto.response.NotificationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications")
public class NotificationController {

    private final GetNotificationsUseCase getNotificationsUseCase;
    private final MarkNotificationsReadUseCase markNotificationsReadUseCase;

    @GetMapping
    public List<NotificationResponse> getNotifications(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return getNotificationsUseCase.getForUser(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/unread-count")
    public long getUnreadCount(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return getNotificationsUseCase.countUnread(userId);
    }

    @PatchMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markOneRead(@PathVariable UUID id, Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        markNotificationsReadUseCase.markOne(id, userId);
    }

    @PatchMapping("/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAllRead(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        markNotificationsReadUseCase.markAll(userId);
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(), n.getType(), n.getMessage(),
                n.getReferenceId(), n.isRead(), n.getCreatedAt());
    }
}
