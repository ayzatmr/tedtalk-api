package com.io.tedtalks.repository;

import static com.io.tedtalks.jooq.tables.TedTalks.TED_TALKS;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.sum;

import com.io.tedtalks.dto.InfluentialTalkDto;
import com.io.tedtalks.dto.SpeakerInfluenceDto;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

/**
 * Repository class for performing influence analysis on TED Talks data using a database. Provides
 * methods to retrieve influential talks and analyze influencers by aggregating views and likes
 * metrics with configurable weights.
 *
 * <p>The calculations focus on defining and sorting influence based on the provided weights for
 * views and likes. Results are returned as DTO objects corresponding to the talks or speakers.
 */
@Repository
@RequiredArgsConstructor
public class InfluenceAnalysisRepository {

  private final DSLContext dsl;

  /**
   * Computes and retrieves a list of the most influential TED Talks based on a weighted score
   * calculated using the number of views and likes.
   *
   * @param viewsWeight the weight assigned to the number of views in calculating the influence score
   * @param likesWeight the weight assigned to the number of likes in calculating the influence score
   * @param limit the maximum number of most influential talks to retrieve
   * @return a list of {@code InfluentialTalkDto} objects representing the most influential TED Talks
   */
  public List<InfluentialTalkDto> findMostInfluentialTalks(
      double viewsWeight, double likesWeight, int limit) {

    var influence =
        TED_TALKS
            .VIEWS
            .cast(Double.class)
            .mul(viewsWeight)
            .plus(TED_TALKS.LIKES.cast(Double.class).mul(likesWeight))
            .as("influence");

    return dsl.select(
            TED_TALKS.ID,
            TED_TALKS.TITLE,
            TED_TALKS.AUTHOR,
            TED_TALKS.YEAR_VALUE.as("yearValue"),
            TED_TALKS.MONTH_VALUE.as("monthValue"),
            TED_TALKS.VIEWS,
            TED_TALKS.LIKES,
            TED_TALKS.LINK,
            influence)
        .from(TED_TALKS)
        .orderBy(influence.desc())
        .limit(limit)
        .fetchInto(InfluentialTalkDto.class);
  }

  /**
   * Retrieves a list of the most influential speakers based on a weighted influence score
   * calculated using the number of views and likes for their TED Talks.
   *
   * @param viewsWeight the weight assigned to the number of views in calculating the influence score
   * @param likesWeight the weight assigned to the number of likes in calculating the influence score
   * @param limit the maximum number of speakers to retrieve
   * @return a list of {@code SpeakerInfluenceDto} objects representing the most influential speakers
   */
  public List<SpeakerInfluenceDto> findMostInfluentialSpeakers(
      double viewsWeight, double likesWeight, int limit) {

    var totalViews = sum(TED_TALKS.VIEWS).as("totalViews");
    var totalLikes = sum(TED_TALKS.LIKES).as("totalLikes");
    var totalInfluence =
        sum(TED_TALKS
                .VIEWS
                .cast(Double.class)
                .mul(viewsWeight)
                .plus(TED_TALKS.LIKES.cast(Double.class).mul(likesWeight)))
            .as("totalInfluence");
    var talkCount = count().as("talkCount");

    return dsl.select(TED_TALKS.AUTHOR, totalViews, totalLikes, totalInfluence, talkCount)
        .from(TED_TALKS)
        .groupBy(TED_TALKS.AUTHOR)
        .orderBy(totalInfluence.desc())
        .limit(limit)
        .fetchInto(SpeakerInfluenceDto.class);
  }

  /**
   * Retrieves the influence details of a specific speaker based on a weighted influence
   * score calculated using the number of views and likes for their TED Talks.
   *
   * @param author the name of the speaker whose influence is being calculated
   * @param viewsWeight the weight assigned to the number of views in calculating the influence score
   * @param likesWeight the weight assigned to the number of likes in calculating the influence score
   * @return an {@code Optional<SpeakerInfluenceDto>} containing the speaker's influence data if found,
   *         or an empty {@code Optional} if no data is available for the specified speaker
   */
  public Optional<SpeakerInfluenceDto> findSpeakerInfluence(
      String author, double viewsWeight, double likesWeight) {

    var totalViews = sum(TED_TALKS.VIEWS).as("totalViews");
    var totalLikes = sum(TED_TALKS.LIKES).as("totalLikes");
    var totalInfluence =
        sum(TED_TALKS
                .VIEWS
                .cast(Double.class)
                .mul(viewsWeight)
                .plus(TED_TALKS.LIKES.cast(Double.class).mul(likesWeight)))
            .as("totalInfluence");
    var talkCount = count().as("talkCount");

    return dsl.select(TED_TALKS.AUTHOR, totalViews, totalLikes, totalInfluence, talkCount)
        .from(TED_TALKS)
        .where(DSL.lower(TED_TALKS.AUTHOR).eq(author.toLowerCase()))
        .groupBy(TED_TALKS.AUTHOR)
        .fetchOptionalInto(SpeakerInfluenceDto.class);
  }

  /**
   * Retrieves a list of the most influential TED Talks for each year, based on a calculated
   * influence score that uses weighted values of views and likes.
   *
   * @param viewsWeight the weight assigned to the number of views in calculating the influence score
   * @param likesWeight the weight assigned to the number of likes in calculating the influence score
   * @return a list of {@code InfluentialTalkDto} objects representing the most influential TED Talk
   *         for each year
   */
  public List<InfluentialTalkDto> findMostInfluentialTalkPerYear(
      double viewsWeight, double likesWeight) {

    var influence =
        TED_TALKS
            .VIEWS
            .cast(Double.class)
            .mul(viewsWeight)
            .plus(TED_TALKS.LIKES.cast(Double.class).mul(likesWeight));

    var rn =
        DSL.rowNumber().over().partitionBy(TED_TALKS.YEAR_VALUE).orderBy(influence.desc()).as("rn");

    var subquery =
        dsl.select(
                TED_TALKS.ID,
                TED_TALKS.TITLE,
                TED_TALKS.AUTHOR,
                TED_TALKS.YEAR_VALUE,
                TED_TALKS.MONTH_VALUE,
                TED_TALKS.VIEWS,
                TED_TALKS.LIKES,
                TED_TALKS.LINK,
                influence.as("influence"),
                rn)
            .from(TED_TALKS)
            .asTable("ranked");

    return dsl.select(
            subquery.field(TED_TALKS.ID).as("id"),
            subquery.field(TED_TALKS.TITLE).as("title"),
            subquery.field(TED_TALKS.AUTHOR).as("author"),
            subquery.field(TED_TALKS.YEAR_VALUE).as("yearValue"),
            subquery.field(TED_TALKS.MONTH_VALUE).as("monthValue"),
            subquery.field(TED_TALKS.VIEWS).as("views"),
            subquery.field(TED_TALKS.LIKES).as("likes"),
            subquery.field(TED_TALKS.LINK).as("link"),
            subquery.field("influence", Double.class).as("influence"))
        .from(subquery)
        .where(subquery.field("rn", Integer.class).eq(1))
        .orderBy(subquery.field(TED_TALKS.YEAR_VALUE))
        .fetchInto(InfluentialTalkDto.class);
  }
}
