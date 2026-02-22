package com.fintrack.web.dto.response;

import java.util.List;

public record TransactionPageResponse(
        List<TransactionResponse> content,
        long totalElements,
        int totalPages,
        int page,
        int size
) {}
