package com.assignment.weather.controller;

import com.assignment.weather.dto.MetricsRequest;
import com.assignment.weather.dto.MetricsResponse;
import com.assignment.weather.service.WeatherMetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/v1/weather/metrics")
@Slf4j
@AllArgsConstructor
public class WeatherMetricsController {

    private final WeatherMetricsService metricsService;

    @PostMapping
    @Operation(
            summary = "Query aggregated sensor metrics",
            description = "Query aggregated sensor metrics over a date range."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Metrics returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<List<MetricsResponse>> getMetrics(@RequestBody @Valid final MetricsRequest request) {
        log.info("Received get metrics request for sensor ids={}", request.getSensorIds());
        final List<MetricsResponse> responses = metricsService.getSensorMetrics(request);
        log.info("Metrics: {} fetched successfully", responses.size());
        return ResponseEntity.ok(responses);
    }
}
