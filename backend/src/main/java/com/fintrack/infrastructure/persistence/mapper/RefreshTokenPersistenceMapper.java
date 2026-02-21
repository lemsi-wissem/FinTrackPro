package com.fintrack.infrastructure.persistence.mapper;

import com.fintrack.domain.model.RefreshToken;
import com.fintrack.infrastructure.persistence.entity.RefreshTokenEntity;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenPersistenceMapper {

    public RefreshTokenEntity toEntity(RefreshToken domain) {
        return RefreshTokenEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .tokenHash(domain.getTokenHash())
                .expiresAt(domain.getExpiresAt())
                .revoked(domain.isRevoked())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public RefreshToken toDomain(RefreshTokenEntity entity) {
        return RefreshToken.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getTokenHash(),
                entity.getExpiresAt(),
                entity.isRevoked(),
                entity.getCreatedAt()
        );
    }
}
