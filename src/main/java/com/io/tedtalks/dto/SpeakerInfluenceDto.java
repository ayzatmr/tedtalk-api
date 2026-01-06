package com.io.tedtalks.dto;

public record SpeakerInfluenceDto(
    String author, Long totalViews, Long totalLikes, Double totalInfluence, Long talkCount) {}
