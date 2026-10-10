package com.github.vslyusarev.warehousealerts.shared.contracts;

import java.time.Instant;

public record SensorEvent(
    String warehouseId,
    String sensorId,
    SensorType sensorType,
    int value,
    Instant timestamp
) {
}
