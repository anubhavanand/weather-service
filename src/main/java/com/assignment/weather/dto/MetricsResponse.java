package com.assignment.weather.dto;

import com.assignment.weather.model.MetricType;

/**
 * Response sent to client for metric request
 * @param sensorId
 * @param metricsType
 * @param metricsValue
 */
public record MetricsResponse (long sensorId, MetricType metricsType, double metricsValue){}
