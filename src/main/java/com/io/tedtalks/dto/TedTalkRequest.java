package com.io.tedtalks.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.YearMonth;

/** Represents a request to create or update a TED Talk entry. */
public record TedTalkRequest(
    @NotBlank @Size(max = 500) String title,
    @NotBlank @Size(max = 200) String author,
    @NotNull
        @JsonFormat(pattern = "yyyy-MM")
        @Schema(type = "string", pattern = "^\\d{4}-\\d{2}$", example = "2024-03")
        YearMonth date,
    @Min(0) long views,
    @Min(0) long likes,
    @NotBlank @Size(max = 1000) String link) {}
