package com.github.vslyusarev.warehousealerts.warehouse.streaming.output;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorMeasurement;

import java.time.Instant;

public record CorrelationMetadata(
        String sensorId,
        Instant timestamp
) {
    public CorrelationMetadata(SensorMeasurement sensorMeasurement) {
        this(sensorMeasurement.sensorId(), sensorMeasurement.timestamp());
    }
}
