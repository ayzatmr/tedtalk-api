package com.io.tedtalks.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.YearMonth;

/** Represents a response containing the details of a TED Talk. */
public record TedTalkResponse(
    Long id,
    String title,
    String author,
    @JsonFormat(pattern = "yyyy-MM") YearMonth date,
    long views,
    long likes,
    String link,
    double influenceScore) {}
