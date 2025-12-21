package com.io.tedtalks.dto;

import java.util.List;

/**
 * Represents the aggregated influence response of a speaker.
 *
 * <p>The response is designed to provide a consolidated view of a speaker's influence based on
 * their TED Talks.
 */
public record SpeakerInfluenceResponse(
    String author,
    double totalInfluence,
    long totalViews,
    long totalLikes,
    long talkCount,
    List<TedTalkResponse> talks) {}
