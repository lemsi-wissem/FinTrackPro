package com.fintrack.application.usecase;

import com.fintrack.domain.exception.UserNotFoundException;
import com.fintrack.domain.port.in.MarkNotificationsReadUseCase;
import com.fintrack.domain.port.out.NotificationRepositoryPort;

import java.util.UUID;

public class MarkNotificationsReadUseCaseImpl implements MarkNotificationsReadUseCase {

    private final NotificationRepositoryPort notificationRepo;

    public MarkNotificationsReadUseCaseImpl(NotificationRepositoryPort notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    @Override
    public void markOne(UUID notificationId, UUID userId) {
        notificationRepo.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new UserNotFoundException("Notification not found"));
        notificationRepo.markAsRead(notificationId);
    }

    @Override
    public void markAll(UUID userId) {
        notificationRepo.markAllAsReadForUser(userId);
    }
}
