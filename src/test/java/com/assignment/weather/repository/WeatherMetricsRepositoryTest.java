package com.assignment.weather.repository;

import com.assignment.weather.entity.Metric;
import com.assignment.weather.model.MetricType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WeatherMetricsRepositoryTest {

    @Autowired
    private WeatherMetricsRepository repository;

    private Instant testStartTime;
    private Instant testEndTime;

    @BeforeEach
    void setUp() {
        testStartTime = Instant.EPOCH; // Broad range guarantees match with @CreationTimestamp values
        testEndTime = Instant.parse("2100-01-01T00:00:00Z");
        
        // Clear existing data
        repository.deleteAll();
        
        // Test data - @CreationTimestamp will set current time (covered by broad range)
        createTestMetric("m1", 1L, MetricType.TEMPERATURE, 25.5);
        createTestMetric("m2", 1L, MetricType.TEMPERATURE, 26.0);
        createTestMetric("m3", 1L, MetricType.HUMIDITY, 65.0);
        createTestMetric("m4", 2L, MetricType.TEMPERATURE, 22.0);
        createTestMetric("m5", 2L, MetricType.HUMIDITY, 70.0);
    }

    private void createTestMetric(String id, Long sensorId, MetricType type, double value) {
        Metric metric = new Metric();
        metric.setId(id);
        metric.setSensorId(sensorId);
        metric.setMetricType(type);
        metric.setMetricValue(value);
        repository.save(metric);
    }

    @Test
    void findMaxValuePerSensorAndMetric_shouldReturnMaximumValues() {
        List<WeatherMetricStatisticProjection> results = repository.findMaxValuePerSensorAndMetric(
                List.of(1L, 2L),
                List.of(MetricType.TEMPERATURE, MetricType.HUMIDITY),
                testStartTime,
                testEndTime
        );

        assertEquals(4, results.size());
        
        // Verify max values for all sensor-metric combinations
        assertTrue(results.stream().anyMatch(r -> 
                r.getSensorId() == 1L && r.getMetricType() == MetricType.TEMPERATURE && Math.abs(r.getValue() - 26.0) < 0.001));
        assertTrue(results.stream().anyMatch(r -> 
                r.getSensorId() == 1L && r.getMetricType() == MetricType.HUMIDITY && Math.abs(r.getValue() - 65.0) < 0.001));
        assertTrue(results.stream().anyMatch(r -> 
                r.getSensorId() == 2L && r.getMetricType() == MetricType.TEMPERATURE && Math.abs(r.getValue() - 22.0) < 0.001));
        assertTrue(results.stream().anyMatch(r -> 
                r.getSensorId() == 2L && r.getMetricType() == MetricType.HUMIDITY && Math.abs(r.getValue() - 70.0) < 0.001));
    }

    @Test
    void findMinValuePerSensorAndMetric_shouldReturnMinimumValues() {
        List<WeatherMetricStatisticProjection> results = repository.findMinValuePerSensorAndMetric(
                null, // null = all sensors
                List.of(MetricType.TEMPERATURE),
                testStartTime,
                testEndTime
        );

        assertEquals(2, results.size());
        
        WeatherMetricStatisticProjection sensor1Result = results.stream()
                .filter(r -> r.getSensorId() == 1L)
                .findFirst()
                .orElseThrow();
        WeatherMetricStatisticProjection sensor2Result = results.stream()
                .filter(r -> r.getSensorId() == 2L)
                .findFirst()
                .orElseThrow();

        assertEquals(25.5, sensor1Result.getValue(), 0.001);
        assertEquals(22.0, sensor2Result.getValue(), 0.001);
    }

    @Test
    void findAvgValuePerSensorAndMetric_shouldReturnAverageValues() {
        List<WeatherMetricStatisticProjection> results = repository.findAvgValuePerSensorAndMetric(
                List.of(1L),
                List.of(MetricType.TEMPERATURE),
                testStartTime,
                testEndTime
        );

        assertEquals(1, results.size());
        WeatherMetricStatisticProjection result = results.get(0);
        assertEquals(1L, result.getSensorId());
        assertEquals(MetricType.TEMPERATURE, result.getMetricType());
        assertEquals(25.75, result.getValue(), 0.001); // (25.5 + 26.0) / 2
    }

    @Test
    void findSumValuePerSensorAndMetric_shouldReturnSumValues() {
        List<WeatherMetricStatisticProjection> results = repository.findSumValuePerSensorAndMetric(
                null,
                List.of(MetricType.HUMIDITY),
                testStartTime,
                testEndTime
        );

        assertEquals(2, results.size());
        
        WeatherMetricStatisticProjection sensor1 = results.stream()
                .filter(r -> r.getSensorId() == 1L)
                .findFirst()
                .orElseThrow();
        WeatherMetricStatisticProjection sensor2 = results.stream()
                .filter(r -> r.getSensorId() == 2L)
                .findFirst()
                .orElseThrow();
        
        assertEquals(65.0, sensor1.getValue(), 0.001);
        assertEquals(70.0, sensor2.getValue(), 0.001);
    }

    @Test
    void findLatestBySensorIdInAndMetricTypeIn_shouldReturnMostRecentRecords() {
        List<Metric> results = repository.findLatestBySensorIdInAndMetricTypeIn(
                List.of(1L, 2L),
                List.of(MetricType.TEMPERATURE, MetricType.HUMIDITY)
        );

        assertEquals(4, results.size());
        
        // Verify latest temperature for sensor 1 is 26.0 (not the earlier 25.5)
        boolean hasLatestTempForSensor1 = results.stream()
                .anyMatch(m -> m.getSensorId() == 1L 
                        && m.getMetricType() == MetricType.TEMPERATURE 
                        && Math.abs(m.getMetricValue() - 26.0) < 0.001);
        assertTrue(hasLatestTempForSensor1);
        
        long humidityCount = results.stream()
                .filter(m -> m.getMetricType() == MetricType.HUMIDITY)
                .count();
        assertEquals(2, humidityCount);
    }

    @Test
    void findTopBySensorIdAndMetricTypeOrderByTimestampDesc_shouldReturnLatestRecord() {
        Optional<Metric> result = repository.findTopBySensorIdAndMetricTypeOrderByTimestampDesc(
                1L, MetricType.TEMPERATURE);
        
        assertTrue(result.isPresent());
        Metric metric = result.get();
        assertEquals(1L, metric.getSensorId());
        assertEquals(MetricType.TEMPERATURE, metric.getMetricType());
        assertEquals(26.0, metric.getMetricValue(), 0.001);
        assertNotNull(metric.getTimestamp());
    }

    @Test
    void findTopBySensorIdAndMetricTypeOrderByTimestampDesc_shouldReturnEmptyWhenNoData() {
        Optional<Metric> result = repository.findTopBySensorIdAndMetricTypeOrderByTimestampDesc(
                999L, MetricType.TEMPERATURE);
        assertTrue(result.isEmpty());
    }

    @Test
    void statisticQueries_withNullSensorIds_shouldReturnDataForAllSensors() {
        List<WeatherMetricStatisticProjection> results = repository.findMaxValuePerSensorAndMetric(
                null,
                List.of(MetricType.TEMPERATURE, MetricType.HUMIDITY),
                testStartTime,
                testEndTime
        );

        assertEquals(4, results.size());
    }

    @Test
    void queries_withNoMatchingData_shouldReturnEmptyLists() {
        List<WeatherMetricStatisticProjection> maxResults = repository.findMaxValuePerSensorAndMetric(
                List.of(999L),
                List.of(MetricType.TEMPERATURE),
                testStartTime,
                testEndTime
        );
        
        List<Metric> latestResults = repository.findLatestBySensorIdInAndMetricTypeIn(
                List.of(999L),
                List.of(MetricType.TEMPERATURE)
        );

        assertTrue(maxResults.isEmpty());
        assertTrue(latestResults.isEmpty());
    }

    @Test
    void queries_withDateRangeOutsideData_shouldReturnEmptyResults() {
        Instant futureStart = Instant.parse("2100-01-01T00:00:00Z");
        Instant futureEnd = futureStart.plusSeconds(3600);
        
        List<WeatherMetricStatisticProjection> results = repository.findMaxValuePerSensorAndMetric(
                null,
                List.of(MetricType.TEMPERATURE),
                futureStart,
                futureEnd
        );

        assertTrue(results.isEmpty());
    }
}