package com.assignment.weather.entity;

import com.assignment.weather.model.MetricType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "metric", indexes = {
    // Primary index for the main read query: sensor + type + timestamp (most selective first)
    @Index(name = "idx_metric_sensor_type_timestamp", columnList = "sensor_id, metric_type, timestamp"),
    // Additional index for other common read patterns in time-series data
    @Index(name = "idx_metric_sensor_type", columnList = "sensor_id, metric_type")
})
@Getter
@Setter
public class Metric {
    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(name = "sensor_id", nullable = false, updatable = false)
    private long sensorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "metric_type", nullable = false, updatable = false)
    private MetricType metricType;

    @Column(name = "metric_value", nullable = false, updatable = false)
    private double metricValue;

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
}
