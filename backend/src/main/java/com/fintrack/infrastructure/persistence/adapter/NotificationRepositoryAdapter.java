package com.fintrack.infrastructure.persistence.adapter;

import com.fintrack.domain.model.Notification;
import com.fintrack.domain.port.out.NotificationRepositoryPort;
import com.fintrack.infrastructure.persistence.mapper.NotificationPersistenceMapper;
import com.fintrack.infrastructure.persistence.repository.JpaNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {

    private final JpaNotificationRepository jpaRepository;
    private final NotificationPersistenceMapper mapper;

    @Override
    public Notification save(Notification notification) {
        var entity = mapper.toEntity(notification);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId) {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Notification> findByIdAndUserId(UUID id, UUID userId) {
        return jpaRepository.findByIdAndUserId(id, userId).map(mapper::toDomain);
    }

    @Override
    public void markAsRead(UUID id) {
        jpaRepository.markAsRead(id);
    }

    @Override
    public void markAllAsReadForUser(UUID userId) {
        jpaRepository.markAllAsReadForUser(userId);
    }

    @Override
    public long countUnreadByUserId(UUID userId) {
        return jpaRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    public boolean existsByUserIdAndTypeAndReferenceIdAndCreatedAtBetween(
            UUID userId, String type, UUID referenceId, LocalDateTime from, LocalDateTime to) {
        return jpaRepository.existsByUserIdAndTypeAndReferenceIdAndCreatedAtBetween(
                userId, type, referenceId, from, to);
    }
}
