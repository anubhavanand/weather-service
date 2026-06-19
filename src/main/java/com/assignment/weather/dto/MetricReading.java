package com.assignment.weather.dto;

import com.assignment.weather.model.MetricType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

/**
 * Reading sent by sensors.
 */
@AllArgsConstructor
@Getter
public class MetricReading {
    @NotNull(message = "Metric type is required")
    private MetricType metricType;
    @NotNull(message = "Metric value is required")
    private double metricValue;
    private Instant recordedAt;
}
