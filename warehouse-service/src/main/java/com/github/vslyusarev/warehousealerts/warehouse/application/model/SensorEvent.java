package com.github.vslyusarev.warehousealerts.warehouse.application.model;

import java.time.Instant;

public record SensorEvent(
    String warehouseId,
    String sensorId,
    SensorType sensorType,
    int value,
    Instant timestamp
) {
}
