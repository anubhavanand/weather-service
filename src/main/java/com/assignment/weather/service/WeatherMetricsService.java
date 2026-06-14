package com.assignment.weather.service;

import com.assignment.weather.dto.MetricsRequest;
import com.assignment.weather.dto.MetricsResponse;
import com.assignment.weather.entity.Metric;
import com.assignment.weather.model.MetricType;
import com.assignment.weather.repository.WeatherMetricStatisticProjection;
import com.assignment.weather.repository.WeatherMetricsRepository;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Service responsible for retrieving and processing weather metric data.
 * Provides functionality to fetch aggregated statistics (min, max, avg, sum)
 * for a given date range, as well as the most recent metric values per sensor.
 * Results for latest metrics are cached for improved performance.
 */
@Slf4j
@Service
public class WeatherMetricsService {

    private static final String SEPARATOR = "::_::";

    private final ZoneId zone = ZoneOffset.UTC;

    private final WeatherMetricsRepository repository;

    private final LoadingCache<String, Optional<MetricsResponse>> cache;

    @Autowired
    public WeatherMetricsService(
            final WeatherMetricsRepository repository,
            @Value("${cache.expire-after-write-minutes:1}") final long expireAfterWriteMinutes,
            @Value("${cache.maximum-size:100}") final long maximumSize) {
        this.repository = repository;
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(expireAfterWriteMinutes, TimeUnit.MINUTES)
                .maximumSize(maximumSize)
                .build(CacheLoader.from(key -> key != null ? queryLatestMetric(key) : Optional.empty()));
    }

    public List<MetricsResponse> getSensorMetrics(final MetricsRequest request) {
        log.info("Received request to fetch metrics. Date range provides: {}", request.getStartDate() != null);
        return request.getStartDate() != null ? getMetricsInDateRange(request) :
                getLatestMetrics(request);
    }

    private List<MetricsResponse> getMetricsInDateRange(final MetricsRequest request) {
        log.info("Fetching metrics for statistics type: {}", request.getStatisticType());
        final List<Long> sensorIds = CollectionUtils.isEmpty(request.getSensorIds()) ? null : request.getSensorIds();

        final List<MetricType> metricTypes = request.getMetricsTypes();
        final Instant startDate = request.getStartDate().atStartOfDay(zone).toInstant();
        final Instant endDate = request.getEndDate().plusDays(1).atStartOfDay(zone).toInstant();
        final List<WeatherMetricStatisticProjection> metrics =
                switch (request.getStatisticType()) {
                    case MIN -> repository.findMinValuePerSensorAndMetric(sensorIds, metricTypes, startDate, endDate);
                    case MAX -> repository.findMaxValuePerSensorAndMetric(sensorIds, metricTypes, startDate, endDate);
                    case SUM -> repository.findSumValuePerSensorAndMetric(sensorIds, metricTypes, startDate, endDate);
                    case AVG -> repository.findAvgValuePerSensorAndMetric(sensorIds, metricTypes, startDate, endDate);
                };
        log.info("{} Metrics received.", metrics.size());
        return metrics.stream()
                .map(metric -> new MetricsResponse(metric.getSensorId(), metric.getMetricType(), metric.getValue()))
                .toList();
    }

    private List<MetricsResponse> getLatestMetrics(final MetricsRequest request) {
        log.info("Fetching latest metrics as statistics type is not provided.");
        if (CollectionUtils.isEmpty(request.getSensorIds())) {
            log.info("No Sensor ids provided, fetching metrics for all sensors.");
            return getLatestMetricsForMultiple(request);
        }

        final List<MetricsResponse> responses = new ArrayList<>();
        for (final long sensorId : request.getSensorIds()) {
            for (final MetricType metricsType : request.getMetricsTypes()) {
                final String key = sensorId + SEPARATOR + metricsType;
                try {
                    Optional<MetricsResponse> metricsResponse = cache.get(key);
                    metricsResponse.ifPresent(responses::add);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return responses;
    }

    private Optional<MetricsResponse> queryLatestMetric(final String key) {
        log.info("Cache miss for key: {}, querying database.", key);
        final String[] splits = key.split(SEPARATOR);
        final long sensorId = Long.parseLong(splits[0]);
        final MetricType metricsType = MetricType.valueOf(splits[1]);
        return repository.findTopBySensorIdAndMetricTypeOrderByTimestampDesc(sensorId, metricsType)
                .map(metric -> new MetricsResponse(
                        metric.getSensorId(),
                        metric.getMetricType(),
                        metric.getMetricValue()
                ));
    }

    private List<MetricsResponse> getLatestMetricsForMultiple(final MetricsRequest request) {
        log.info("Fetching Latest Metrics for multiple sensor ids");
        final List<Long> sensorIds = CollectionUtils.isEmpty(request.getSensorIds()) ? null : request.getSensorIds();
        final List<Metric> metrics = repository.findLatestBySensorIdInAndMetricTypeIn(
                sensorIds, request.getMetricsTypes());

        return metrics.stream()
                .map(metric -> new MetricsResponse(
                        metric.getSensorId(),
                        metric.getMetricType(),
                        metric.getMetricValue()
                ))
                .toList();
    }
}
