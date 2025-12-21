package com.io.tedtalks.dto;

/**
 * Represents the response containing yearly influence data for a TED Talk.
 *
 * @param year the year for which influence data is represented
 * @param mostInfluentialTalk the TED Talk that had the highest influence score for the year
 */
public record YearlyInfluenceResponse(int year, TedTalkResponse mostInfluentialTalk) {}
