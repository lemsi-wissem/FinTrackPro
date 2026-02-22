package com.fintrack.application.usecase;

import com.fintrack.domain.model.Transaction;
import com.fintrack.domain.model.TransactionType;
import com.fintrack.domain.port.in.ImportTransactionsUseCase;
import com.fintrack.domain.port.out.CategoryRepositoryPort;
import com.fintrack.domain.port.out.TransactionRepositoryPort;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ImportTransactionsUseCaseImpl implements ImportTransactionsUseCase {

    private final TransactionRepositoryPort transactionRepo;
    private final CategoryRepositoryPort categoryRepo;

    public ImportTransactionsUseCaseImpl(TransactionRepositoryPort transactionRepo,
                                          CategoryRepositoryPort categoryRepo) {
        this.transactionRepo = transactionRepo;
        this.categoryRepo = categoryRepo;
    }

    @Override
    public ImportResult importCsv(UUID userId, byte[] csvBytes) {
        List<RowError> errors = new ArrayList<>();
        int imported = 0;
        int skipped = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(csvBytes), StandardCharsets.UTF_8))) {

            String header = reader.readLine();
            if (header == null) {
                return new ImportResult(0, 0, List.of(new RowError(0, "Empty file")));
            }

            String line;
            int rowNum = 1;
            while ((line = reader.readLine()) != null) {
                rowNum++;
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] cols = parseCsvLine(line);
                // Expected: date, type, amount, category (optional), description (optional), notes (optional)
                if (cols.length < 3) {
                    errors.add(new RowError(rowNum, "Not enough columns (need at least: date, type, amount)"));
                    continue;
                }

                try {
                    LocalDate date = LocalDate.parse(cols[0].trim());
                    TransactionType type = TransactionType.valueOf(cols[1].trim().toUpperCase());
                    BigDecimal amount = new BigDecimal(cols[2].trim());

                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        errors.add(new RowError(rowNum, "Amount must be positive"));
                        continue;
                    }

                    UUID categoryId = null;
                    if (cols.length > 3 && !cols[3].trim().isEmpty()) {
                        String catName = cols[3].trim();
                        categoryId = categoryRepo.findByUserIdAndName(userId, catName)
                                .map(c -> c.getId())
                                .orElse(null);
                    }

                    String description = cols.length > 4 ? cols[4].trim() : null;
                    String notes = cols.length > 5 ? cols[5].trim() : null;

                    // Duplicate check: same date + amount + description for this user
                    if (transactionRepo.existsDuplicate(userId, date, amount, description)) {
                        skipped++;
                        continue;
                    }

                    Transaction tx = Transaction.create(userId, categoryId, amount, type,
                            description, date, notes);
                    transactionRepo.save(tx);
                    imported++;

                } catch (DateTimeParseException e) {
                    errors.add(new RowError(rowNum, "Invalid date format (expected YYYY-MM-DD): " + cols[0]));
                } catch (NumberFormatException e) {
                    errors.add(new RowError(rowNum, "Invalid amount: " + cols[2]));
                } catch (IllegalArgumentException e) {
                    errors.add(new RowError(rowNum, "Invalid type (expected INCOME or EXPENSE): " + cols[1]));
                }
            }
        } catch (Exception e) {
            errors.add(new RowError(0, "Failed to parse CSV: " + e.getMessage()));
        }

        return new ImportResult(imported, skipped, errors);
    }

    /** Handles quoted fields with commas inside them. */
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }
}
