package com.assignment.weather.entity;

import jakarta.persistence.Id;

public class Sensor {
    @Id
    private int id;
    private double latitude;
    private double longitude;
    private int batteryPercentage;
}
