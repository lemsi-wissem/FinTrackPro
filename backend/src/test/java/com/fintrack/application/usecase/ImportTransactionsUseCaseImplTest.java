package com.fintrack.application.usecase;

import com.fintrack.domain.model.Category;
import com.fintrack.domain.model.TransactionType;
import com.fintrack.domain.port.in.ImportTransactionsUseCase.ImportResult;
import com.fintrack.domain.port.out.CategoryRepositoryPort;
import com.fintrack.domain.port.out.TransactionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImportTransactionsUseCaseImplTest {

    @Mock
    private TransactionRepositoryPort transactionRepo;

    @Mock
    private CategoryRepositoryPort categoryRepo;

    private ImportTransactionsUseCaseImpl useCase;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new ImportTransactionsUseCaseImpl(transactionRepo, categoryRepo);
    }

    private byte[] csv(String... lines) {
        return String.join("\n", lines).getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void importCsv_withEmptyFile_shouldReturnErrorResult() {
        ImportResult result = useCase.importCsv(userId, new byte[0]);

        assertThat(result.imported()).isEqualTo(0);
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).row()).isEqualTo(0);
        assertThat(result.errors().get(0).message()).contains("Empty file");
        verify(transactionRepo, never()).save(any());
    }

    @Test
    void importCsv_withOnlyHeader_shouldImportZeroRows() {
        byte[] data = csv("date,type,amount,category,description,notes");

        ImportResult result = useCase.importCsv(userId, data);

        assertThat(result.imported()).isEqualTo(0);
        assertThat(result.skipped()).isEqualTo(0);
        assertThat(result.errors()).isEmpty();
    }

    @Test
    void importCsv_withValidRows_shouldImportAllRows() {
        byte[] data = csv(
                "date,type,amount",
                "2026-01-15,EXPENSE,100.00",
                "2026-01-20,INCOME,500.00"
        );

        when(transactionRepo.existsDuplicate(any(), any(), any(), any())).thenReturn(false);

        ImportResult result = useCase.importCsv(userId, data);

        assertThat(result.imported()).isEqualTo(2);
        assertThat(result.errors()).isEmpty();
        verify(transactionRepo, times(2)).save(any());
    }

    @Test
    void importCsv_withInvalidDateFormat_shouldAddRowError() {
        byte[] data = csv(
                "date,type,amount",
                "15/01/2026,EXPENSE,100.00"
        );

        ImportResult result = useCase.importCsv(userId, data);

        assertThat(result.imported()).isEqualTo(0);
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).row()).isEqualTo(2);
        assertThat(result.errors().get(0).message()).contains("Invalid date format");
    }

    @Test
    void importCsv_withInvalidTransactionType_shouldAddRowError() {
        byte[] data = csv(
                "date,type,amount",
                "2026-01-15,INVALID_TYPE,100.00"
        );

        ImportResult result = useCase.importCsv(userId, data);

        assertThat(result.imported()).isEqualTo(0);
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).message()).contains("Invalid type");
    }

    @Test
    void importCsv_withNonNumericAmount_shouldAddRowError() {
        byte[] data = csv(
                "date,type,amount",
                "2026-01-15,EXPENSE,abc"
        );

        ImportResult result = useCase.importCsv(userId, data);

        assertThat(result.imported()).isEqualTo(0);
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).message()).contains("Invalid amount");
    }

    @Test
    void importCsv_withNegativeAmount_shouldAddRowError() {
        byte[] data = csv(
                "date,type,amount",
                "2026-01-15,EXPENSE,-50.00"
        );

        ImportResult result = useCase.importCsv(userId, data);

        assertThat(result.imported()).isEqualTo(0);
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).message()).contains("Amount must be positive");
    }

    @Test
    void importCsv_withZeroAmount_shouldAddRowError() {
        byte[] data = csv(
                "date,type,amount",
                "2026-01-15,EXPENSE,0"
        );

        ImportResult result = useCase.importCsv(userId, data);

        assertThat(result.imported()).isEqualTo(0);
        assertThat(result.errors()).hasSize(1);
    }

    @Test
    void importCsv_withTooFewColumns_shouldAddRowError() {
        byte[] data = csv(
                "date,type",
                "2026-01-15,EXPENSE"
        );

        ImportResult result = useCase.importCsv(userId, data);

        assertThat(result.imported()).isEqualTo(0);
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).message()).contains("Not enough columns");
    }

    @Test
    void importCsv_withDuplicate_shouldSkipAndNotImport() {
        byte[] data = csv(
                "date,type,amount",
                "2026-01-15,EXPENSE,100.00"
        );

        when(transactionRepo.existsDuplicate(any(), any(), any(), any())).thenReturn(true);

        ImportResult result = useCase.importCsv(userId, data);

        assertThat(result.imported()).isEqualTo(0);
        assertThat(result.skipped()).isEqualTo(1);
        verify(transactionRepo, never()).save(any());
    }

    @Test
    void importCsv_withCategoryName_shouldLookUpCategoryByName() {
        UUID catId = UUID.randomUUID();
        Category cat = Category.reconstitute(catId, userId, "Food", TransactionType.EXPENSE,
                "#FF0000", null, false, LocalDateTime.now(), LocalDateTime.now());

        byte[] data = csv(
                "date,type,amount,category",
                "2026-01-15,EXPENSE,100.00,Food"
        );

        when(categoryRepo.findByUserIdAndName(userId, "Food")).thenReturn(Optional.of(cat));
        when(transactionRepo.existsDuplicate(any(), any(), any(), any())).thenReturn(false);

        ImportResult result = useCase.importCsv(userId, data);

        assertThat(result.imported()).isEqualTo(1);
        verify(categoryRepo).findByUserIdAndName(userId, "Food");
    }

    @Test
    void importCsv_withUnknownCategory_shouldImportWithNullCategoryId() {
        byte[] data = csv(
                "date,type,amount,category",
                "2026-01-15,EXPENSE,100.00,UnknownCat"
        );

        when(categoryRepo.findByUserIdAndName(userId, "UnknownCat")).thenReturn(Optional.empty());
        when(transactionRepo.existsDuplicate(any(), any(), any(), any())).thenReturn(false);

        ImportResult result = useCase.importCsv(userId, data);

        assertThat(result.imported()).isEqualTo(1);
        assertThat(result.errors()).isEmpty();
    }

    @Test
    void importCsv_withMixedValidAndInvalidRows_shouldCountCorrectly() {
        byte[] data = csv(
                "date,type,amount",
                "2026-01-15,EXPENSE,100.00",    // valid → imported
                "bad-date,EXPENSE,200.00",        // invalid date → error
                "2026-01-17,INCOME,300.00",       // valid → imported
                "2026-01-18,EXPENSE,-10.00"       // negative → error
        );

        when(transactionRepo.existsDuplicate(any(), any(), any(), any())).thenReturn(false);

        ImportResult result = useCase.importCsv(userId, data);

        assertThat(result.imported()).isEqualTo(2);
        assertThat(result.errors()).hasSize(2);
        verify(transactionRepo, times(2)).save(any());
    }

    @Test
    void importCsv_withCaseSensitiveType_shouldNormalizeToUpperCase() {
        byte[] data = csv(
                "date,type,amount",
                "2026-01-15,expense,150.00"
        );

        when(transactionRepo.existsDuplicate(any(), any(), any(), any())).thenReturn(false);

        ImportResult result = useCase.importCsv(userId, data);

        assertThat(result.imported()).isEqualTo(1);
        assertThat(result.errors()).isEmpty();
    }
}
