package com.io.tedtalks.entity;

import com.io.tedtalks.dto.TedTalkRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.YearMonth;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

  /**
   * Creates a new instance of {@code TedTalkEntity} based on the properties of the provided {@code
   * TedTalkRequest}.
   *
   * @param request the {@code TedTalkRequest} containing the data required to create the entity
   * @return a newly created {@code TedTalkEntity} populated with the values from the provided
   *     request
   */
  public static TedTalkEntity of(TedTalkRequest request) {
    return of(
        request.title(),
        request.author(),
        request.date(),
        request.views(),
        request.likes(),
        request.link());
  }

  /**
   * Creates a new instance of {@code TedTalkEntity} based on the specified properties.
   *
   * @param title the title of the TED Talk; must not be null or blank
   * @param author the author or speaker of the TED Talk; must not be null or blank
   * @param yearMonth the year and month when the TED Talk was presented; must not be null
   * @param views the number of views the TED Talk has received; must be non-negative
   * @param likes the number of likes the TED Talk has received; must be non-negative
   * @param link the link to the TED Talk; must not be null or blank
   * @return a new {@code TedTalkEntity} instance populated with the specified properties
   */
  public static TedTalkEntity of(
      String title, String author, YearMonth yearMonth, long views, long likes, String link) {

    TedTalkEntity entity = new TedTalkEntity();
    entity.title = title.trim();
    entity.author = author.trim();
    entity.year = yearMonth.getYear();
    entity.month = yearMonth.getMonthValue();
    entity.views = Math.max(0, views);
    entity.likes = Math.max(0, likes);
    entity.link = link.trim();

    return entity;
  }

  /**
   * Calculates the influence of a TED Talk based on its views and likes, weighted by the given
   * factors.
   *
   * @param viewsWeight the weight to apply to the number of views
   * @param likesWeight the weight to apply to the number of likes
   * @return the calculated influence as a double value
   */
  public double calculateInfluence(double viewsWeight, double likesWeight) {
    return (views * viewsWeight) + (likes * likesWeight);
  }

  /**
   * Retrieves the year and month of the TED Talk as a {@code YearMonth} instance.
   *
   * @return a {@code YearMonth} representing the year and month of the TED Talk
   */
  public YearMonth getYearMonth() {
    return YearMonth.of(year, month);
  }

  /**
   * Sets the year and month of the TED Talk entity based on the provided {@code YearMonth}
   * instance.
   *
   * @param yearMonth the {@code YearMonth} representing the year and month to set; must not be null
   */
  public void setYearMonth(YearMonth yearMonth) {
    this.year = yearMonth.getYear();
    this.month = yearMonth.getMonthValue();
  }

  /**
   * Updates the properties of this {@code TedTalkEntity} with the values from the provided {@code
   * TedTalkRequest}. The request must not be null. Trims leading and trailing whitespace from
   * string values and directly sets numeric values.
   *
   * @param request the {@code TedTalkRequest} containing the updated data for the TED Talk entity;
   *     must not be null
   * @throws IllegalArgumentException if the provided request is null
   */
  public void updateFrom(TedTalkRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("Request must not be null");
    }

    this.title = request.title().trim();
    this.author = request.author().trim();
    setYearMonth(request.date());
    this.views = request.views();
    this.likes = request.likes();
    this.link = request.link().trim();
  }

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
