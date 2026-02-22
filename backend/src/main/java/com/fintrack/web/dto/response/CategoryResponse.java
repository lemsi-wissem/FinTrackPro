package com.fintrack.web.dto.response;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        UUID userId,
        String name,
        String type,
        String color,
        String icon,
        boolean system
) {}
