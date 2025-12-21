package com.io.tedtalks.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.io.tedtalks.dto.PagedResponse;
import com.io.tedtalks.dto.TedTalkRequest;
import com.io.tedtalks.dto.TedTalkResponse;
import com.io.tedtalks.service.TedTalkService;
import java.time.InstantSource;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TedTalkController.class)
final class TedTalkControllerTest {

  private static final String BASE_URL = "/api/v1/talks";

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private TedTalkService tedTalkService;

  @MockitoBean private InstantSource instantSource;

  private static TedTalkRequest createRequest() {
    return new TedTalkRequest(
        "Test Talk", "John Doe", YearMonth.of(2020, 1), 1000, 100, "http://test.com");
  }

  private static TedTalkResponse createResponse() {
    return new TedTalkResponse(
        1L, "Test Talk", "John Doe", YearMonth.of(2020, 1), 1000, 100, "http://test.com", 800.0);
  }

  @Test
  void createTalk_shouldReturnCreated() throws Exception {
    TedTalkRequest request = createRequest();
    TedTalkResponse response = createResponse();

    when(tedTalkService.createTalk(any(TedTalkRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.title").value("Test Talk"))
        .andExpect(jsonPath("$.author").value("John Doe"));
  }

  @Test
  void getTalks_shouldReturnPagedResponse() throws Exception {
    TedTalkResponse response = createResponse();

    PagedResponse<TedTalkResponse> pagedResponse =
        PagedResponse.of(List.of(response), new PageImpl<>(List.of(response)));

    when(tedTalkService.getTalks(any(), any(), any(), any(Pageable.class)))
        .thenReturn(pagedResponse);

    mockMvc
        .perform(get(BASE_URL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.rows").isArray())
        .andExpect(jsonPath("$.rows.length()").value(1))
        .andExpect(jsonPath("$.rows[0].title").value("Test Talk"));
  }

  @Test
  void getTalkById_shouldReturnTalk() throws Exception {
    when(tedTalkService.getTalkById(1L)).thenReturn(createResponse());

    mockMvc
        .perform(get(BASE_URL + "/{id}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.title").value("Test Talk"));
  }

  @Test
  void updateTalk_shouldReturnUpdatedTalk() throws Exception {
    TedTalkRequest request =
        new TedTalkRequest(
            "Updated Talk", "Jane Doe", YearMonth.of(2021, 5), 2000, 200, "http://updated.com");

    TedTalkResponse response =
        new TedTalkResponse(
            1L,
            "Updated Talk",
            "Jane Doe",
            YearMonth.of(2021, 5),
            2000,
            200,
            "http://updated.com",
            1600.0);

    when(tedTalkService.updateTalk(1L, request)).thenReturn(response);

    mockMvc
        .perform(
            put(BASE_URL + "/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Updated Talk"))
        .andExpect(jsonPath("$.author").value("Jane Doe"));
  }

  @Test
  void deleteTalk_shouldReturnNoContent() throws Exception {
    doNothing().when(tedTalkService).deleteTalk(1L);

    mockMvc.perform(delete(BASE_URL + "/{id}", 1L)).andExpect(status().isNoContent());
  }
}
