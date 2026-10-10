package com.github.vslyusarev.warehousealerts.central.application.model;

import java.time.Instant;

public record Alert(
        String warehouseId,
        String sensorId,
        Instant timestamp,
        int threshold,
        int actualValue
) {
}
