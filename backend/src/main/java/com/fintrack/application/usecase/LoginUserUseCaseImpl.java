package com.fintrack.application.usecase;

import com.fintrack.domain.exception.AccountDisabledException;
import com.fintrack.domain.exception.AuthenticationFailedException;
import com.fintrack.domain.exception.EmailNotVerifiedException;
import com.fintrack.domain.model.RefreshToken;
import com.fintrack.domain.model.User;
import com.fintrack.domain.port.in.LoginUserUseCase;
import com.fintrack.domain.port.out.PasswordEncoderPort;
import com.fintrack.domain.port.out.RefreshTokenRepositoryPort;
import com.fintrack.domain.port.out.TokenProviderPort;
import com.fintrack.domain.port.out.UserRepositoryPort;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

public class LoginUserUseCaseImpl implements LoginUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final long refreshTokenExpirationMs;

    public LoginUserUseCaseImpl(UserRepositoryPort userRepository,
                                 PasswordEncoderPort passwordEncoder,
                                 TokenProviderPort tokenProvider,
                                 RefreshTokenRepositoryPort refreshTokenRepository,
                                 long refreshTokenExpirationMs) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    @Override
    public AuthTokens login(LoginCommand command) {
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(AuthenticationFailedException::new);

        if (!user.isVerified()) {
            throw new EmailNotVerifiedException();
        }

        if (!user.isActive()) {
            throw new AccountDisabledException();
        }

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new AuthenticationFailedException();
        }

        String accessToken = tokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String rawRefreshToken = tokenProvider.generateRefreshToken();
        String refreshTokenHash = hashToken(rawRefreshToken);

        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000);
        RefreshToken refreshToken = RefreshToken.create(user.getId(), refreshTokenHash, expiresAt);
        refreshTokenRepository.save(refreshToken);

        return new AuthTokens(accessToken, rawRefreshToken);
    }

    static String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
