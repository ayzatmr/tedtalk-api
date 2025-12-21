package com.io.tedtalks.dto;

/** Projection interface for speaker influence data retrieved from native SQL queries. */
public interface SpeakerInfluenceDto {
  String getAuthor();

  Long getTotalViews();

  Long getTotalLikes();

  Double getTotalInfluence();

  Long getTalkCount();
}
