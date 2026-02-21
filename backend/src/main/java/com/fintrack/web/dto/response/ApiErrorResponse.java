package com.fintrack.web.dto.response;

import java.time.LocalDateTime;

public record ApiErrorResponse(int status, String error, String message, LocalDateTime timestamp) {}
