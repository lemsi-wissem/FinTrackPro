package com.fintrack.domain.port.in;

import java.util.List;
import java.util.UUID;

public interface ImportTransactionsUseCase {

    ImportResult importCsv(UUID userId, byte[] csvBytes);

    record ImportResult(int imported, int skipped, List<RowError> errors) {}

    record RowError(int row, String message) {}
}
