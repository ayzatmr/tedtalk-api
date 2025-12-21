package com.io.tedtalks.dto;

/** Projection interface for influential TED Talk data retrieved from native SQL queries. */
public interface InfluentialTalkDto {
  Long getId();

  String getTitle();

  String getAuthor();

  Integer getYearValue();

  Integer getMonthValue();

  Long getViews();

  Long getLikes();

  String getLink();

  Double getInfluence();
}
