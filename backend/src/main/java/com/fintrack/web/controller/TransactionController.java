package com.fintrack.web.controller;

import com.fintrack.domain.exception.TransactionNotFoundException;
import com.fintrack.domain.model.Category;
import com.fintrack.domain.model.Transaction;
import com.fintrack.domain.model.TransactionType;
import com.fintrack.domain.port.in.CreateTransactionUseCase;
import com.fintrack.domain.port.in.CreateTransactionUseCase.CreateTransactionCommand;
import com.fintrack.domain.port.in.DeleteTransactionUseCase;
import com.fintrack.domain.port.in.GetTransactionsUseCase;
import com.fintrack.domain.port.in.GetTransactionsUseCase.TransactionFilter;
import com.fintrack.domain.port.in.ImportTransactionsUseCase;
import com.fintrack.domain.port.in.UpdateTransactionUseCase;
import com.fintrack.domain.port.in.UpdateTransactionUseCase.UpdateTransactionCommand;
import com.fintrack.domain.port.out.CategoryRepositoryPort;
import com.fintrack.domain.port.out.FileStoragePort;
import com.fintrack.domain.port.out.TransactionRepositoryPort;
import com.fintrack.web.dto.request.CreateTransactionRequest;
import com.fintrack.web.dto.request.UpdateTransactionRequest;
import com.fintrack.web.dto.response.ImportResultResponse;
import com.fintrack.web.dto.response.TransactionPageResponse;
import com.fintrack.web.dto.response.TransactionResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions")
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final GetTransactionsUseCase getTransactionsUseCase;
    private final UpdateTransactionUseCase updateTransactionUseCase;
    private final DeleteTransactionUseCase deleteTransactionUseCase;
    private final ImportTransactionsUseCase importTransactionsUseCase;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final TransactionRepositoryPort transactionRepositoryPort;
    private final FileStoragePort fileStoragePort;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@Valid @RequestBody CreateTransactionRequest request,
                                                  Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        Transaction transaction = createTransactionUseCase.create(new CreateTransactionCommand(
                userId, request.categoryId(), request.amount(), request.type(),
                request.description(), request.transactionDate(), request.notes()));
        return toResponse(transaction, resolveCategory(request.categoryId()));
    }

    @GetMapping
    public TransactionPageResponse getTransactions(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        TransactionFilter filter = new TransactionFilter(type, categoryId, from, to, page, size);

        List<Transaction> transactions = getTransactionsUseCase.getForUser(userId, filter);
        long totalElements = getTransactionsUseCase.countForUser(userId, filter);

        Map<UUID, Optional<Category>> categoryCache = new HashMap<>();
        List<TransactionResponse> content = transactions.stream()
                .map(t -> {
                    Optional<Category> cat = categoryCache.computeIfAbsent(
                            t.getCategoryId(), this::resolveCategory);
                    return toResponse(t, cat);
                })
                .collect(Collectors.toList());

        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        return new TransactionPageResponse(content, totalElements, totalPages, page, size);
    }

    @PutMapping("/{id}")
    public TransactionResponse updateTransaction(@PathVariable UUID id,
                                                  @Valid @RequestBody UpdateTransactionRequest request,
                                                  Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        Transaction transaction = updateTransactionUseCase.update(new UpdateTransactionCommand(
                id, userId, request.categoryId(), request.amount(),
                request.description(), request.transactionDate(), request.notes()));
        return toResponse(transaction, resolveCategory(request.categoryId()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable UUID id, Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        deleteTransactionUseCase.delete(id, userId);
    }

    @PostMapping("/{id}/attachment")
    public TransactionResponse uploadAttachment(@PathVariable UUID id,
                                                 @RequestParam("file") MultipartFile file,
                                                 Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        Transaction transaction = transactionRepositoryPort.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        // Delete old attachment if exists
        if (transaction.getAttachmentUrl() != null) {
            fileStoragePort.delete(transaction.getAttachmentUrl());
        }

        String url = fileStoragePort.store(file, "attachments");
        transaction.setAttachmentUrl(url);
        Transaction saved = transactionRepositoryPort.save(transaction);
        return toResponse(saved, resolveCategory(saved.getCategoryId()));
    }

    @PostMapping("/import")
    public ImportResultResponse importCsv(@RequestParam("file") MultipartFile file,
                                           Authentication authentication) throws IOException {
        UUID userId = (UUID) authentication.getPrincipal();
        ImportTransactionsUseCase.ImportResult result =
                importTransactionsUseCase.importCsv(userId, file.getBytes());

        return new ImportResultResponse(
                result.imported(),
                result.skipped(),
                result.errors().stream()
                        .map(e -> new ImportResultResponse.RowError(e.row(), e.message()))
                        .collect(Collectors.toList()));
    }

    @GetMapping("/import/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        String csv = "date,type,amount,category,description,notes\n" +
                "2024-01-15,EXPENSE,50.00,Food,Grocery shopping,Weekly groceries\n" +
                "2024-01-20,INCOME,3000.00,,Monthly salary,\n";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"import-template.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.getBytes());
    }

    private Optional<Category> resolveCategory(UUID categoryId) {
        if (categoryId == null) return Optional.empty();
        return categoryRepositoryPort.findById(categoryId);
    }

    private TransactionResponse toResponse(Transaction transaction, Optional<Category> category) {
        String categoryName = category.map(Category::getName).orElse(null);
        String categoryColor = category.map(Category::getColor).orElse(null);
        return new TransactionResponse(
                transaction.getId(),
                transaction.getCategoryId(),
                categoryName,
                categoryColor,
                transaction.getAmount(),
                transaction.getType().name(),
                transaction.getDescription(),
                transaction.getTransactionDate(),
                transaction.getNotes(),
                transaction.getAttachmentUrl(),
                transaction.getCreatedAt()
        );
    }
}
