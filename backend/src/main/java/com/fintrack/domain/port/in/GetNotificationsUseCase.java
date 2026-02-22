package com.fintrack.domain.port.in;

import com.fintrack.domain.model.Notification;

import java.util.List;
import java.util.UUID;

public interface GetNotificationsUseCase {
    List<Notification> getForUser(UUID userId);
    long countUnread(UUID userId);
}
