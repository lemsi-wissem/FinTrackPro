package com.fintrack.domain.port.out;

import com.fintrack.domain.model.Notification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepositoryPort {
    Notification save(Notification notification);
    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);
    Optional<Notification> findByIdAndUserId(UUID id, UUID userId);
    void markAsRead(UUID id);
    void markAllAsReadForUser(UUID userId);
    long countUnreadByUserId(UUID userId);
    boolean existsByUserIdAndTypeAndReferenceIdAndCreatedAtBetween(
            UUID userId, String type, UUID referenceId, LocalDateTime from, LocalDateTime to);
}
