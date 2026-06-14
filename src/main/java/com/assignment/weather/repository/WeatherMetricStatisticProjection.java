package com.assignment.weather.repository;

import com.assignment.weather.model.MetricType;

/**
 * Projection interface for retrieving aggregated sensor metric statistics.
 *
 * @see MetricType
 */
public interface WeatherMetricStatisticProjection {
    Long getSensorId();

    MetricType getMetricType();

    Double getValue();
}
