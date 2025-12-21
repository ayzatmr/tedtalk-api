package com.io.tedtalks.controller;

import com.io.tedtalks.dto.SpeakerInfluenceResponse;
import com.io.tedtalks.dto.TedTalkResponse;
import com.io.tedtalks.dto.YearlyInfluenceResponse;
import com.io.tedtalks.exception.ResourceNotFoundException;
import com.io.tedtalks.service.InfluenceAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for handling operations related to speaker and talk influence analysis. */
@RestController
@RequestMapping("/api/v1/influence")
@RequiredArgsConstructor
@Validated
@Tag(name = "Influence Analysis", description = "Speaker and talk influence analysis endpoints")
public class InfluenceAnalysisController {

  private final InfluenceAnalysisService influenceAnalysisService;

  /**
   * Fetches the most influential speakers based on their influence metrics.
   *
   * @param topN the number of top speakers to return; should be a positive integer.
   * @return a list of SpeakerInfluenceResponse objects representing the most influential speakers.
   */
  @GetMapping("/speakers")
  @Operation(summary = "Get most influential speakers")
  public List<SpeakerInfluenceResponse> getMostInfluentialSpeakers(
      @Parameter(description = "Number of top speakers to return")
          @Min(1)
          @Max(100)
          @RequestParam(defaultValue = "5")
          int topN) {
    return influenceAnalysisService.getMostInfluentialSpeakers(topN);
  }

  /**
   * Retrieves the influence metrics of a specific speaker based on the provided author name.
   *
   * @param author the name of the speaker whose influence metrics are to be fetched
   * @throws ResourceNotFoundException if the specified speaker is not found
   */
  @GetMapping("/speaker")
  @Operation(summary = "Get specific speaker's influence")
  public SpeakerInfluenceResponse getSpeakerInfluence(
      @Parameter(description = "Author name") @RequestParam String author) {
    return influenceAnalysisService
        .getSpeakerInfluence(author)
        .orElseThrow(() -> new ResourceNotFoundException("Speaker not found: " + author));
  }

  /**
   * Fetches the most influential TED Talks based on their influence metrics.
   *
   * @param topN the number of top talks to return; should be a positive integer.
   * @return a list of TedTalkResponse objects representing the most influential talks.
   */
  @GetMapping("/talks")
  @Operation(summary = "Get most influential talks")
  public List<TedTalkResponse> getMostInfluentialTalks(
      @Parameter(description = "Number of top talks to return")
          @Min(1)
          @Max(100)
          @RequestParam(defaultValue = "5")
          int topN) {
    return influenceAnalysisService.getMostInfluentialTalks(topN);
  }

  /**
   * Retrieves the most influential TED Talk for each year based on their influence metrics.
   *
   * @return a list of YearlyInfluenceResponse objects, where each object contains the year and the
   *     corresponding most influential TED Talk for that year.
   */
  @GetMapping("/talks/by-year")
  @Operation(summary = "Get most influential talk per year")
  public List<YearlyInfluenceResponse> getMostInfluentialTalkByYear() {
    return influenceAnalysisService.getMostInfluentialTalkByYear();
  }
}
