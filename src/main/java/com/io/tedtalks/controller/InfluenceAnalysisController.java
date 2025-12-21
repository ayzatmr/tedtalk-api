package com.io.tedtalks.controller;

import com.io.tedtalks.service.InfluenceAnalysisService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for handling operations related to speaker and talk influence analysis. */
@RestController
@RequestMapping("/api/v1/influence")
@RequiredArgsConstructor
@Validated
@Tag(name = "Influence Analysis", description = "Speaker and talk influence analysis endpoints")
public class InfluenceAnalysisController {

  private final InfluenceAnalysisService influenceAnalysisService;
}
