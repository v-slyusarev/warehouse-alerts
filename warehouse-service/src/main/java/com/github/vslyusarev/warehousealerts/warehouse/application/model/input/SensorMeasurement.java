package com.github.vslyusarev.warehousealerts.warehouse.application.model.input;

public record SensorMeasurement(
        String sensorId,
        int value
) {

}
