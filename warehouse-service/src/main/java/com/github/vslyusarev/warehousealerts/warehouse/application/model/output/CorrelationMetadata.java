package com.github.vslyusarev.warehousealerts.warehouse.application.model.output;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorEvent;

import java.time.Instant;

public record CorrelationMetadata(
        String sensorId,
        Instant timestamp
) {
    public CorrelationMetadata(SensorEvent sensorEvent) {
        this(sensorEvent.sensorId(), sensorEvent.timestamp());
    }
}
