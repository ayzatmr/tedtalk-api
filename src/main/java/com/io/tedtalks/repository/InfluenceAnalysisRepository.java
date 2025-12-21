package com.io.tedtalks.repository;

import com.io.tedtalks.dto.InfluentialTalkDto;
import com.io.tedtalks.dto.SpeakerInfluenceDto;
import com.io.tedtalks.entity.TedTalkEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

/** Repository interface for managing influence analysis queries on TED Talks data. */
@org.springframework.stereotype.Repository
public interface InfluenceAnalysisRepository extends Repository<TedTalkEntity, Long> {

  /**
   * Retrieves the most influential TED Talks based on a weighted calculation of views and likes.
   *
   * <p>The influence is calculated as: influence = (views * viewsWeight) + (likes * likesWeight).
   * The results are sorted in descending order of influence and limited by the specified limit.
   *
   * @param viewsWeight the weight assigned to the number of views in the influence calculation
   * @param likesWeight the weight assigned to the number of likes in the influence calculation
   * @param limit the maximum number of results to retrieve
   * @return a list of {@code InfluentialTalkDto} objects representing the most influential TED
   *     Talks
   */
  @Query(
      value =
          """
          SELECT
            id,
            title,
            author,
            year_value  AS yearValue,
            month_value AS monthValue,
            views,
            likes,
            link,
            (CAST(views AS DOUBLE) * :viewsWeight + CAST(likes AS DOUBLE) * :likesWeight) AS influence
          FROM ted_talks
          ORDER BY (CAST(views AS DOUBLE) * :viewsWeight + CAST(likes AS DOUBLE) * :likesWeight) DESC
          LIMIT :limit
          """,
      nativeQuery = true)
  List<InfluentialTalkDto> findMostInfluentialTalks(
      @Param("viewsWeight") double viewsWeight,
      @Param("likesWeight") double likesWeight,
      @Param("limit") int limit);

  /**
   * Retrieves a list of the most influential speakers based on the weighted influence.
   *
   * <p>The method executes a query to aggregate and sort TED Talk data by speaker influence,
   * summing views and likes weighted by the specified coefficients, and limits the results to the
   * specified number of top speakers.
   *
   * @param viewsWeight the weight applied to the number of views when calculating influence
   * @param likesWeight the weight applied to the number of likes when calculating influence
   * @param limit the maximum number of speakers to include in the result
   * @return a list of SpeakerInfluenceDto objects representing the most influential speakers
   */
  @Query(
      value =
          """
                SELECT
                  author,
                  totalViews,
                  totalLikes,
                  totalInfluence,
                  talkCount
                FROM (
                  SELECT
                    author,
                    SUM(views) AS totalViews,
                    SUM(likes) AS totalLikes,
                    SUM(CAST(views AS DOUBLE) * :viewsWeight + CAST(likes AS DOUBLE) * :likesWeight) AS totalInfluence,
                    COUNT(*) AS talkCount
                  FROM ted_talks
                  GROUP BY author
                )
                ORDER BY totalInfluence DESC
                LIMIT :limit
          """,
      nativeQuery = true)
  List<SpeakerInfluenceDto> findMostInfluentialSpeakers(
      @Param("viewsWeight") double viewsWeight,
      @Param("likesWeight") double likesWeight,
      @Param("limit") int limit);

  /**
   * Retrieves the influence metrics of a speaker based on TED talk data. The method calculates
   * total views, total likes, total influence, and the count of talks given by the specified
   * speaker, considering the provided weights for views and likes.
   *
   * @param author the name of the speaker whose influence data is to be retrieved; case-insensitive
   * @param viewsWeight the weight applied to the number of views for influence calculation
   * @param likesWeight the weight applied to the number of likes for influence calculation
   * @return an {@code Optional} containing a {@code SpeakerInfluenceDto} with the aggregated
   *     influence data if the speaker exists, or an empty {@code Optional} if no data is found
   */
  @Query(
      value =
          """
          SELECT
            author,
            SUM(views) AS totalViews,
            SUM(likes) AS totalLikes,
            SUM(CAST(views AS DOUBLE) * :viewsWeight + CAST(likes AS DOUBLE) * :likesWeight) AS totalInfluence,
            COUNT(*) AS talkCount
          FROM ted_talks
          WHERE LOWER(author) = LOWER(:author)
          GROUP BY author
          """,
      nativeQuery = true)
  Optional<SpeakerInfluenceDto> findSpeakerInfluence(
      @Param("author") String author,
      @Param("viewsWeight") double viewsWeight,
      @Param("likesWeight") double likesWeight);

  /**
   * Finds the most influential TED Talks for each year based on a weighted combination of the
   * number of views and likes.
   *
   * @param viewsWeight the weight applied to the number of views in the influence calculation
   * @param likesWeight the weight applied to the number of likes in the influence calculation
   * @return a list of {@code InfluentialTalkDto} objects representing the most influential TED Talk
   *     for each year
   */
  @Query(
      value =
          """
            SELECT
              id,
              title,
              author,
              yearValue,
              monthValue,
              views,
              likes,
              link,
              influence
            FROM (
              SELECT
                id,
                title,
                author,
                year_value  AS yearValue,
                month_value AS monthValue,
                views,
                likes,
                link,
                (CAST(views AS DOUBLE) * :viewsWeight + CAST(likes AS DOUBLE) * :likesWeight) AS influence,
                ROW_NUMBER() OVER (
                  PARTITION BY year_value
                  ORDER BY (CAST(views AS DOUBLE) * :viewsWeight + CAST(likes AS DOUBLE) * :likesWeight) DESC
                ) AS rn
              FROM ted_talks
            )
            WHERE rn = 1
            ORDER BY yearValue
          """,
      nativeQuery = true)
  List<InfluentialTalkDto> findMostInfluentialTalkPerYear(
      @Param("viewsWeight") double viewsWeight, @Param("likesWeight") double likesWeight);
}
