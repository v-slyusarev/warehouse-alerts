package com.github.vslyusarev.warehousealerts.warehouse.application.model;

import java.time.Instant;

public record SensorMeasurement(
    String sensorId,
    SensorType sensorType,
    int value,
    Instant timestamp
) {
}
