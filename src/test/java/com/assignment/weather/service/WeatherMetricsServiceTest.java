package com.assignment.weather.service;

import com.assignment.weather.dto.MetricsRequest;
import com.assignment.weather.dto.MetricsResponse;
import com.assignment.weather.entity.Metric;
import com.assignment.weather.model.MetricType;
import com.assignment.weather.model.StatisticType;
import com.assignment.weather.repository.WeatherMetricStatisticProjection;
import com.assignment.weather.repository.WeatherMetricsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherMetricsServiceTest {

    @Mock
    private WeatherMetricsRepository repository;

    private WeatherMetricsService service;

    @BeforeEach
    void setUp() {
        service = new WeatherMetricsService(repository, 1, 10);
    }

    @Test
    void getSensorMetrics_withDates_shouldReturnAggregatedResults() {
        MetricsRequest request = new MetricsRequest();
        request.setSensorIds(List.of(1L));
        request.setMetricsTypes(List.of(MetricType.TEMPERATURE));
        request.setStatisticType(StatisticType.AVG);
        request.setStartDate(LocalDate.now().minusDays(1));
        request.setEndDate(LocalDate.now());

        WeatherMetricStatisticProjection projection = mock(WeatherMetricStatisticProjection.class);
        when(projection.getSensorId()).thenReturn(1L);
        when(projection.getMetricType()).thenReturn(MetricType.TEMPERATURE);
        when(projection.getValue()).thenReturn(20.0);

        when(repository.findAvgValuePerSensorAndMetric(any(), any(), any(), any()))
                .thenReturn(List.of(projection));

        List<MetricsResponse> result = service.getSensorMetrics(request);

        assertEquals(1, result.size());
        assertEquals(20.0, result.getFirst().metricsValue());
    }

    @Test
    void getSensorMetrics_withoutDates_shouldReturnLatestFromCache() {
        MetricsRequest request = new MetricsRequest();
        request.setSensorIds(List.of(1L));
        request.setMetricsTypes(List.of(MetricType.HUMIDITY));

        Metric m = new Metric();
        m.setSensorId(1L);
        m.setMetricType(MetricType.HUMIDITY);
        m.setMetricValue(60.0);

        when(repository.findTopBySensorIdAndMetricTypeOrderByTimestampDesc(anyLong(), any(MetricType.class)))
                .thenReturn(Optional.of(m));

        List<MetricsResponse> result = service.getSensorMetrics(request);

        assertEquals(1, result.size());
    }
}