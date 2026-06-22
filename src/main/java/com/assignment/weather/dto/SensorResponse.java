package com.assignment.weather.dto;

import com.assignment.weather.model.MetricType;
import lombok.Builder;

import java.time.Instant;

@Builder
public record SensorResponse(
        long id,
        MetricType metricType,
        double metricsValue,
        Instant timestamp) {
}
