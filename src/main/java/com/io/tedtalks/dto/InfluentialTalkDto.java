package com.io.tedtalks.dto;

public record InfluentialTalkDto(
    Long id,
    String title,
    String author,
    Integer yearValue,
    Integer monthValue,
    Long views,
    Long likes,
    String link,
    Double influence) {
}
