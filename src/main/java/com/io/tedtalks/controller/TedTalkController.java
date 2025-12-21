package com.io.tedtalks.controller;

import com.io.tedtalks.service.TedTalkService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller class responsible for managing TED Talk resources. */
@RestController
@RequestMapping("/api/v1/talks")
@RequiredArgsConstructor
@Validated
@Tag(name = "TED Talks", description = "TED Talks management endpoints")
public class TedTalkController {

  private final TedTalkService tedTalkService;
}
