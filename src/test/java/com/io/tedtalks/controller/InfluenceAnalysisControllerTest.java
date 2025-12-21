package com.io.tedtalks.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.io.tedtalks.dto.SpeakerInfluenceResponse;
import com.io.tedtalks.dto.TedTalkResponse;
import com.io.tedtalks.dto.YearlyInfluenceResponse;
import com.io.tedtalks.service.InfluenceAnalysisService;
import java.time.InstantSource;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(InfluenceAnalysisController.class)
final class InfluenceAnalysisControllerTest {

  private static final String BASE_URL = "/api/v1/influence";

  @Autowired private MockMvc mockMvc;

  @MockitoBean private InfluenceAnalysisService influenceAnalysisService;

  @MockitoBean private InstantSource instantSource;

  @Test
  void getMostInfluentialSpeakers_shouldReturnList() throws Exception {
    when(influenceAnalysisService.getMostInfluentialSpeakers(5))
        .thenReturn(List.of(speakerResponse()));

    mockMvc
        .perform(get(BASE_URL + "/speakers").param("topN", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].author").value("John Doe"))
        .andExpect(jsonPath("$[0].totalInfluence").value(1000.0));
  }

  @Test
  void getSpeakerInfluence_shouldReturnSpeaker() throws Exception {
    when(influenceAnalysisService.getSpeakerInfluence("John Doe"))
        .thenReturn(Optional.of(speakerResponse()));

    mockMvc
        .perform(get(BASE_URL + "/speaker").param("author", "John Doe"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.author").value("John Doe"))
        .andExpect(jsonPath("$.talkCount").value(3));
  }

  @Test
  void getSpeakerInfluence_shouldReturn404_whenNotFound() throws Exception {
    when(influenceAnalysisService.getSpeakerInfluence("Unknown")).thenReturn(Optional.empty());

    mockMvc
        .perform(get(BASE_URL + "/speaker").param("author", "Unknown"))
        .andExpect(status().isNotFound());
  }

  @Test
  void getMostInfluentialTalks_shouldReturnList() throws Exception {
    when(influenceAnalysisService.getMostInfluentialTalks(5)).thenReturn(List.of(talkResponse()));

    mockMvc
        .perform(get(BASE_URL + "/talks").param("topN", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value("Test Talk"))
        .andExpect(jsonPath("$[0].author").value("John Doe"));
  }

  @Test
  void getMostInfluentialTalkByYear_shouldReturnList() throws Exception {
    YearlyInfluenceResponse response = new YearlyInfluenceResponse(2020, talkResponse());

    when(influenceAnalysisService.getMostInfluentialTalkByYear()).thenReturn(List.of(response));

    mockMvc
        .perform(get(BASE_URL + "/talks/by-year"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].year").value(2020))
        .andExpect(jsonPath("$[0].mostInfluentialTalk.title").value("Test Talk"));
  }


  private static SpeakerInfluenceResponse speakerResponse() {
    return new SpeakerInfluenceResponse("John Doe", 1000.0, 5000, 500, 3, List.of());
  }

  private static TedTalkResponse talkResponse() {
    return new TedTalkResponse(
        1L, "Test Talk", "John Doe", YearMonth.of(2020, 1), 1000, 100, "http://test.com", 800.0);
  }
}
