package com.assignment.weather.dto;

import com.assignment.weather.model.MetricType;
import com.assignment.weather.model.StatisticType;
import com.assignment.weather.validation.ValidDateRange;
import com.assignment.weather.validation.ValidMetricsTypes;
import com.assignment.weather.validation.ValidStatisticType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for weather metrics requests.
 */
@ValidDateRange
@ValidMetricsTypes
@ValidStatisticType
@Getter
@Setter
public class MetricsRequest {
    private List<Long> sensorIds;
    private List<MetricType> metricsTypes;
    private StatisticType statisticType;

    private LocalDate startDate;
    private LocalDate endDate;
}