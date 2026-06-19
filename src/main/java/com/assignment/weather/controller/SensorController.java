package com.assignment.weather.controller;

import com.assignment.weather.dto.IngestionResponse;
import com.assignment.weather.dto.SensorRequest;
import com.assignment.weather.dto.SensorResponse;
import com.assignment.weather.service.SensorReadingIngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 1. Timestamp "recordedAt" in MeterRequest is important since it supports batch
 * 2. BatchId is important
 */
@RestController
@RequestMapping("/v1/sensor/ingest-readings/{sensorId}")
@Slf4j
@RequiredArgsConstructor
public class SensorController {

    private final SensorReadingIngestionService ingestionService;

    @PostMapping
    @Operation(
            summary = "Ingest sensor metrics",
            description = "API to ingest sensor metrics"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Readings ingested successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<IngestionResponse> ingestReadings(@PathVariable Long sensorId,
                                                            @Valid @RequestBody final SensorRequest request) {
        log.info("Received metrics from sensor id={}", sensorId);
        final List<SensorResponse> sensorResponses = ingestionService.ingestReadings(sensorId, request);
        log.info("All Metrics for sensor id={} ingested.", sensorId);
        final IngestionResponse response = new IngestionResponse(sensorId, sensorResponses);
        return ResponseEntity.ok(response);
    }
}
