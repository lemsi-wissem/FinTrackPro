package com.fintrack.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {

    private UUID id;
    private UUID userId;
    private UUID categoryId;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private LocalDate transactionDate;
    private String notes;
    private String attachmentUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Transaction() {}

    public static Transaction create(UUID userId, UUID categoryId, BigDecimal amount,
                                      TransactionType type, String description,
                                      LocalDate transactionDate, String notes) {
        Transaction t = new Transaction();
        t.id = UUID.randomUUID();
        t.userId = userId;
        t.categoryId = categoryId;
        t.amount = amount;
        t.type = type;
        t.description = description;
        t.transactionDate = transactionDate;
        t.notes = notes;
        t.createdAt = LocalDateTime.now();
        t.updatedAt = LocalDateTime.now();
        return t;
    }

    public static Transaction reconstitute(UUID id, UUID userId, UUID categoryId, BigDecimal amount,
                                            TransactionType type, String description,
                                            LocalDate transactionDate, String notes,
                                            String attachmentUrl,
                                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        Transaction t = new Transaction();
        t.id = id;
        t.userId = userId;
        t.categoryId = categoryId;
        t.amount = amount;
        t.type = type;
        t.description = description;
        t.transactionDate = transactionDate;
        t.notes = notes;
        t.attachmentUrl = attachmentUrl;
        t.createdAt = createdAt;
        t.updatedAt = updatedAt;
        return t;
    }

    public void update(UUID categoryId, BigDecimal amount, String description,
                        LocalDate transactionDate, String notes) {
        this.categoryId = categoryId;
        this.amount = amount;
        this.description = description;
        this.transactionDate = transactionDate;
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getCategoryId() { return categoryId; }
    public BigDecimal getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public String getDescription() { return description; }
    public LocalDate getTransactionDate() { return transactionDate; }
    public String getNotes() { return notes; }
    public String getAttachmentUrl() { return attachmentUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
