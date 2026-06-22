package com.assignment.weather.dto;

import com.assignment.weather.model.MetricType;
import com.assignment.weather.model.StatisticType;
import com.assignment.weather.validation.ValidDateRange;
import com.assignment.weather.validation.ValidStatisticType;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for weather metrics requests.
 */
@ValidDateRange
@ValidStatisticType
@Getter
@Setter
public class MetricsRequest {
    private List<Long> sensorIds;
    @NotEmpty(message = "At least one metric type is required")
    private List<MetricType> metricsTypes;
    private StatisticType statisticType;

    private LocalDate startDate;
    private LocalDate endDate;
}