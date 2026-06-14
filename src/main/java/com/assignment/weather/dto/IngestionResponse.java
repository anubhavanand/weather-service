package com.assignment.weather.dto;

import java.util.List;

/**
 * Response sent to client after readings are ingested.
 * @param sensorId
 * @param sensorResponse
 */
public record IngestionResponse(Long sensorId, List<SensorResponse> sensorResponse) {}
