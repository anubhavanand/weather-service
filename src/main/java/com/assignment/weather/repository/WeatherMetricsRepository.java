package com.assignment.weather.repository;

import com.assignment.weather.entity.Metric;
import com.assignment.weather.model.MetricType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing and querying {@link Metric} entities.
 * Provides methods for retrieving aggregated statistics (min, max, avg, sum)
 * as well as the most recent metric records per sensor and metric type.
 * @see Metric
 * @see WeatherMetricStatisticProjection
 */
@Repository
public interface WeatherMetricsRepository extends JpaRepository<Metric, String> {

	@Query("""
        SELECT
            m.sensorId AS sensorId,
            m.metricType AS metricType,
            MAX(m.metricValue) AS value
        FROM Metric m
        WHERE (:sensorIds IS NULL OR m.sensorId IN :sensorIds)
          AND m.metricType IN :metricTypes
          AND m.timestamp BETWEEN :startDate AND :endDate
        GROUP BY m.sensorId, m.metricType
   """)
	List<WeatherMetricStatisticProjection> findMaxValuePerSensorAndMetric(
			@Param("sensorIds") List<Long> sensorIds,
			@Param("metricTypes") List<MetricType> metricTypes,
			@Param("startDate") Instant startDate,
			@Param("endDate") Instant endDate
	);

	@Query("""
        SELECT
            m.sensorId AS sensorId,
            m.metricType AS metricType,
            MIN(m.metricValue) AS value
        FROM Metric m
        WHERE (:sensorIds IS NULL OR m.sensorId IN :sensorIds)
          AND m.metricType IN :metricTypes
          AND m.timestamp BETWEEN :startDate AND :endDate
        GROUP BY m.sensorId, m.metricType
    """)
	List<WeatherMetricStatisticProjection> findMinValuePerSensorAndMetric(
			@Param("sensorIds") List<Long> sensorIds,
			@Param("metricTypes") List<MetricType> metricTypes,
			@Param("startDate") Instant startDate,
			@Param("endDate") Instant endDate
	);

	@Query("""
        SELECT
            m.sensorId AS sensorId,
            m.metricType AS metricType,
            AVG(m.metricValue) AS value
        FROM Metric m
        WHERE (:sensorIds IS NULL OR m.sensorId IN :sensorIds)
          AND m.metricType IN :metricTypes
          AND m.timestamp BETWEEN :startDate AND :endDate
        GROUP BY m.sensorId, m.metricType
    """)
	List<WeatherMetricStatisticProjection> findAvgValuePerSensorAndMetric(
			@Param("sensorIds") List<Long> sensorIds,
			@Param("metricTypes") List<MetricType> metricTypes,
			@Param("startDate") Instant startDate,
			@Param("endDate") Instant endDate
	);

	@Query("""
        SELECT
            m.sensorId AS sensorId,
            m.metricType AS metricType,
            SUM(m.metricValue) AS value
        FROM Metric m
        WHERE (:sensorIds IS NULL OR m.sensorId IN :sensorIds)
          AND m.metricType IN :metricTypes
          AND m.timestamp BETWEEN :startDate AND :endDate
        GROUP BY m.sensorId, m.metricType
    """)
	List<WeatherMetricStatisticProjection> findSumValuePerSensorAndMetric(
			@Param("sensorIds") List<Long> sensorIds,
			@Param("metricTypes") List<MetricType> metricTypes,
			@Param("startDate") Instant startDate,
			@Param("endDate") Instant endDate
	);

	@Query("""
			SELECT m FROM Metric m
			WHERE (:sensorIds IS NULL OR m.sensorId IN :sensorIds)
			  AND m.metricType IN :metricsTypes
			  AND m.timestamp = (
			      SELECT MAX(m2.timestamp)
			      FROM Metric m2
			      WHERE m2.sensorId = m.sensorId
			        AND m2.metricType = m.metricType
			  )
			""")
	List<Metric> findLatestBySensorIdInAndMetricTypeIn(
			@Param("sensorIds") List<Long> sensorIds,
			@Param("metricsTypes") List<MetricType> metricsTypes);

	Optional<Metric> findTopBySensorIdAndMetricTypeOrderByTimestampDesc(
			long sensorId,
			MetricType metricType
	);
}
