package com.assignment.weather.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * List of readings sent by sensor
 */
@AllArgsConstructor
@Getter
public class SensorRequest {
    @NotEmpty(message = "At least one metric reading is required")
    @Valid
    private List<MetricReading> metricReadings;
}
