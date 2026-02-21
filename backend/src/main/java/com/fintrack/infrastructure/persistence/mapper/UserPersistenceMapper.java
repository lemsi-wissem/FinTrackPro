package com.fintrack.infrastructure.persistence.mapper;

import com.fintrack.domain.model.Role;
import com.fintrack.domain.model.User;
import com.fintrack.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public UserEntity toEntity(User domain) {
        return UserEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .passwordHash(domain.getPasswordHash())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .role(domain.getRole().name())
                .verified(domain.isVerified())
                .active(domain.isActive())
                .oauthProvider(domain.getOauthProvider())
                .oauthId(domain.getOauthId())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public User toDomain(UserEntity entity) {
        return User.reconstitute(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getFirstName(),
                entity.getLastName(),
                Role.valueOf(entity.getRole()),
                entity.isVerified(),
                entity.isActive(),
                entity.getOauthProvider(),
                entity.getOauthId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
