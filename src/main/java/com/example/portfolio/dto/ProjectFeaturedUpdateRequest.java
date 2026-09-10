package com.example.portfolio.dto;

import jakarta.validation.constraints.NotNull;

public record ProjectFeaturedUpdateRequest(@NotNull Boolean featured) {
}
