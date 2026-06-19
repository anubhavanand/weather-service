package com.assignment.weather.dto;

import com.assignment.weather.model.Location;

/**
 * Sensor class
 * @param id
 * @param location
 * @param batteryLevel
 */
public record Sensor(long id, Location location, int batteryLevel) {
}
