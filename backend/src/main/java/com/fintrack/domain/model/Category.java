package com.fintrack.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Category {

    private UUID id;
    private UUID userId;
    private String name;
    private TransactionType type;
    private String color;
    private String icon;
    private boolean system;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Category() {}

    public static Category create(UUID userId, String name, TransactionType type, String color, String icon) {
        Category c = new Category();
        c.id = UUID.randomUUID();
        c.userId = userId;
        c.name = name;
        c.type = type;
        c.color = color;
        c.icon = icon;
        c.system = false;
        c.createdAt = LocalDateTime.now();
        c.updatedAt = LocalDateTime.now();
        return c;
    }

    public static Category reconstitute(UUID id, UUID userId, String name, TransactionType type,
                                         String color, String icon, boolean system,
                                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        Category c = new Category();
        c.id = id;
        c.userId = userId;
        c.name = name;
        c.type = type;
        c.color = color;
        c.icon = icon;
        c.system = system;
        c.createdAt = createdAt;
        c.updatedAt = updatedAt;
        return c;
    }

    public void update(String name, String color, String icon) {
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getName() { return name; }
    public TransactionType getType() { return type; }
    public String getColor() { return color; }
    public String getIcon() { return icon; }
    public boolean isSystem() { return system; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
