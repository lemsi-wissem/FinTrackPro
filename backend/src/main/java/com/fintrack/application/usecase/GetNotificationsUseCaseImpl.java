package com.fintrack.application.usecase;

import com.fintrack.domain.model.Notification;
import com.fintrack.domain.port.in.GetNotificationsUseCase;
import com.fintrack.domain.port.out.NotificationRepositoryPort;

import java.util.List;
import java.util.UUID;

public class GetNotificationsUseCaseImpl implements GetNotificationsUseCase {

    private final NotificationRepositoryPort notificationRepo;

    public GetNotificationsUseCaseImpl(NotificationRepositoryPort notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    @Override
    public List<Notification> getForUser(UUID userId) {
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public long countUnread(UUID userId) {
        return notificationRepo.countUnreadByUserId(userId);
    }
}
