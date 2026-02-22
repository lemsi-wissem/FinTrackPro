package com.fintrack.web.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String type,
        String message,
        UUID referenceId,
        boolean read,
        LocalDateTime createdAt
) {}
