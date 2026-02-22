package com.fintrack.infrastructure.persistence.mapper;

import com.fintrack.domain.model.Notification;
import com.fintrack.infrastructure.persistence.entity.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationPersistenceMapper {

    public NotificationEntity toEntity(Notification domain) {
        return NotificationEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .type(domain.getType())
                .message(domain.getMessage())
                .referenceId(domain.getReferenceId())
                .read(domain.isRead())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public Notification toDomain(NotificationEntity entity) {
        return Notification.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getType(),
                entity.getMessage(),
                entity.getReferenceId(),
                entity.isRead(),
                entity.getCreatedAt()
        );
    }
}
