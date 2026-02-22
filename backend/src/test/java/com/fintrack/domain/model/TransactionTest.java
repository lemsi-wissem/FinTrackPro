package com.fintrack.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionTest {

    private final UUID userId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();

    @Test
    void create_shouldInitializeFieldsAndGenerateId() {
        var amount = new BigDecimal("150.00");
        var date = LocalDate.of(2026, 1, 15);

        Transaction tx = Transaction.create(userId, categoryId, amount,
                TransactionType.EXPENSE, "Groceries", date, "Weekly shop");

        assertThat(tx.getId()).isNotNull();
        assertThat(tx.getUserId()).isEqualTo(userId);
        assertThat(tx.getCategoryId()).isEqualTo(categoryId);
        assertThat(tx.getAmount()).isEqualByComparingTo(amount);
        assertThat(tx.getType()).isEqualTo(TransactionType.EXPENSE);
        assertThat(tx.getDescription()).isEqualTo("Groceries");
        assertThat(tx.getTransactionDate()).isEqualTo(date);
        assertThat(tx.getNotes()).isEqualTo("Weekly shop");
        assertThat(tx.getAttachmentUrl()).isNull();
        assertThat(tx.getCreatedAt()).isNotNull();
        assertThat(tx.getUpdatedAt()).isNotNull();
    }

    @Test
    void create_twoTransactions_shouldHaveDifferentIds() {
        var date = LocalDate.now();
        Transaction t1 = Transaction.create(userId, categoryId, BigDecimal.TEN, TransactionType.INCOME, "A", date, null);
        Transaction t2 = Transaction.create(userId, categoryId, BigDecimal.TEN, TransactionType.INCOME, "B", date, null);
        assertThat(t1.getId()).isNotEqualTo(t2.getId());
    }

    @Test
    void update_shouldModifyFieldsAndUpdateTimestamp() throws InterruptedException {
        Transaction tx = Transaction.create(userId, categoryId,
                new BigDecimal("100.00"), TransactionType.EXPENSE, "Old desc", LocalDate.now(), null);
        var newCategoryId = UUID.randomUUID();
        var newDate = LocalDate.of(2026, 2, 1);
        Thread.sleep(5);

        tx.update(newCategoryId, new BigDecimal("200.00"), "New desc", newDate, "Updated notes");

        assertThat(tx.getCategoryId()).isEqualTo(newCategoryId);
        assertThat(tx.getAmount()).isEqualByComparingTo("200.00");
        assertThat(tx.getDescription()).isEqualTo("New desc");
        assertThat(tx.getTransactionDate()).isEqualTo(newDate);
        assertThat(tx.getNotes()).isEqualTo("Updated notes");
    }

    @Test
    void setAttachmentUrl_shouldUpdateUrlAndTimestamp() throws InterruptedException {
        Transaction tx = Transaction.create(userId, null,
                BigDecimal.ONE, TransactionType.INCOME, "Test", LocalDate.now(), null);
        assertThat(tx.getAttachmentUrl()).isNull();
        Thread.sleep(5);

        tx.setAttachmentUrl("https://s3.example.com/receipt.pdf");

        assertThat(tx.getAttachmentUrl()).isEqualTo("https://s3.example.com/receipt.pdf");
    }

    @Test
    void reconstitute_shouldPreserveAllFields() {
        var id = UUID.randomUUID();
        var now = java.time.LocalDateTime.now();
        var date = LocalDate.of(2026, 3, 10);

        Transaction tx = Transaction.reconstitute(id, userId, categoryId,
                new BigDecimal("500.00"), TransactionType.INCOME,
                "Salary", date, "Monthly salary", "https://url.com", now, now);

        assertThat(tx.getId()).isEqualTo(id);
        assertThat(tx.getAmount()).isEqualByComparingTo("500.00");
        assertThat(tx.getType()).isEqualTo(TransactionType.INCOME);
        assertThat(tx.getDescription()).isEqualTo("Salary");
        assertThat(tx.getAttachmentUrl()).isEqualTo("https://url.com");
    }
}
