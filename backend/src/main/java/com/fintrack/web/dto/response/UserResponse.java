package com.fintrack.web.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String role,
        boolean verified,
        LocalDateTime createdAt
) {}
