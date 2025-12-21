package com.io.tedtalks.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Represents a TED Talk entity in the system. */
@Entity
@Table(
    name = "ted_talks",
    indexes = {
      @Index(name = "idx_author", columnList = "author"),
      @Index(name = "idx_year", columnList = "year_value"),
      @Index(name = "idx_views", columnList = "views"),
      @Index(name = "idx_likes", columnList = "likes")
    })
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TedTalkEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String author;

  @Column(name = "year_value", nullable = false)
  private int year;

  @Column(name = "month_value", nullable = false)
  private int month;

  @Column(nullable = false)
  private long views;

  @Column(nullable = false)
  private long likes;

  @Column(nullable = false)
  private String link;

  @PrePersist
  @PreUpdate
  private void validate() {
    if (title == null || title.isBlank()) {
      throw new IllegalStateException("Title is required");
    }
    if (author == null || author.isBlank()) {
      throw new IllegalStateException("Author is required");
    }
    if (link == null || link.isBlank()) {
      throw new IllegalStateException("Link is required");
    }
    if (month < 1 || month > 12) {
      throw new IllegalStateException("Invalid month: " + month);
    }

    title = title.trim();
    author = author.trim();
    link = link.trim();
    views = Math.max(0, views);
    likes = Math.max(0, likes);
  }
}
