package com.fintrack.infrastructure.persistence.repository;

import com.fintrack.infrastructure.persistence.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaNotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    List<NotificationEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<NotificationEntity> findByIdAndUserId(UUID id, UUID userId);

    long countByUserIdAndReadFalse(UUID userId);

    @Modifying
    @Transactional
    @Query("UPDATE NotificationEntity n SET n.read = true WHERE n.id = :id")
    void markAsRead(@Param("id") UUID id);

    @Modifying
    @Transactional
    @Query("UPDATE NotificationEntity n SET n.read = true WHERE n.userId = :userId")
    void markAllAsReadForUser(@Param("userId") UUID userId);

    boolean existsByUserIdAndTypeAndReferenceIdAndCreatedAtBetween(
            UUID userId, String type, UUID referenceId, LocalDateTime from, LocalDateTime to);
}
