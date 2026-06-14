package com.assignment.weather.service;

import com.assignment.weather.dto.SensorRequest;
import com.assignment.weather.dto.SensorResponse;
import com.assignment.weather.entity.Metric;
import com.assignment.weather.repository.WeatherMetricsRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service responsible for ingesting sensor metric readings.
 */
@Slf4j
@Service
@AllArgsConstructor
public class SensorReadingIngestionService {

    private final WeatherMetricsRepository repository;
    @Transactional
    public List<SensorResponse> ingestReadings(final Long sensorId, final SensorRequest request) {
        log.info("Received request for ingesting metrics reading for sensor: {}", sensorId);
        final List<Metric> metricsToSave = createMetrics(sensorId, request);
        final List<Metric> savedMetrics = repository.saveAll(metricsToSave);
        log.info("Metrics readings ingested successfully for sensor: {}", sensorId);
        return savedMetrics.stream().map(this::toResponse).toList();
    }

    private List<Metric> createMetrics(final Long sensorId, final SensorRequest request) {
        final List<Metric> metrics = new ArrayList<>();
        request.getMetricReadings().forEach(reading -> {
            final String metricId = UUID.randomUUID().toString();
            final Metric metric = new Metric();
            metric.setId(metricId);
            metric.setSensorId(sensorId);
            metric.setMetricType(reading.getMetricType());
            metric.setMetricValue(reading.getMetricValue());
            metric.setTimestamp(Instant.now());
            metrics.add(metric);
        });
        return metrics;
    }

    private SensorResponse toResponse(final Metric metrics) {
        return SensorResponse.builder()
                .id(metrics.getSensorId())
                .metricType(metrics.getMetricType())
                .metricsValue(metrics.getMetricValue())
                .timestamp(metrics.getTimestamp())
                .build();
    }
}
