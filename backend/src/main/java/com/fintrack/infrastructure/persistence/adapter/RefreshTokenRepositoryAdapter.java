package com.fintrack.infrastructure.persistence.adapter;

import com.fintrack.domain.model.RefreshToken;
import com.fintrack.domain.port.out.RefreshTokenRepositoryPort;
import com.fintrack.infrastructure.persistence.mapper.RefreshTokenPersistenceMapper;
import com.fintrack.infrastructure.persistence.repository.JpaRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final JpaRefreshTokenRepository jpaRepository;
    private final RefreshTokenPersistenceMapper mapper;

    @Override
    public RefreshToken save(RefreshToken token) {
        var entity = mapper.toEntity(token);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void revokeAllByUserId(UUID userId) {
        jpaRepository.revokeAllByUserId(userId);
    }
}
