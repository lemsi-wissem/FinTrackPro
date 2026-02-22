package com.fintrack.domain.port.in;

import java.util.UUID;

public interface MarkNotificationsReadUseCase {
    void markOne(UUID notificationId, UUID userId);
    void markAll(UUID userId);
}
