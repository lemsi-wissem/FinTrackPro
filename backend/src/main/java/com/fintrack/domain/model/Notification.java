package com.fintrack.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notification {

    private UUID id;
    private UUID userId;
    private String type;
    private String message;
    private UUID referenceId;
    private boolean read;
    private LocalDateTime createdAt;

    private Notification() {}

    public static Notification create(UUID userId, String type, String message, UUID referenceId) {
        Notification n = new Notification();
        n.id = UUID.randomUUID();
        n.userId = userId;
        n.type = type;
        n.message = message;
        n.referenceId = referenceId;
        n.read = false;
        n.createdAt = LocalDateTime.now();
        return n;
    }

    public static Notification reconstitute(UUID id, UUID userId, String type, String message,
                                             UUID referenceId, boolean read, LocalDateTime createdAt) {
        Notification n = new Notification();
        n.id = id;
        n.userId = userId;
        n.type = type;
        n.message = message;
        n.referenceId = referenceId;
        n.read = read;
        n.createdAt = createdAt;
        return n;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public UUID getReferenceId() { return referenceId; }
    public boolean isRead() { return read; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
