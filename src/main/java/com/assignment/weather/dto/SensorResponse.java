package com.assignment.weather.dto;

import com.assignment.weather.model.MetricType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@Getter
@Builder
public class SensorResponse {
    private long id;
    private MetricType metricType;
    private double metricsValue;
    private Instant timestamp;
}
