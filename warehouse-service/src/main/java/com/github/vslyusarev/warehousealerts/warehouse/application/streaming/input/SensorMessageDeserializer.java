package com.github.vslyusarev.warehousealerts.warehouse.application.streaming.input;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorMeasurement;

public interface SensorMessageDeserializer {
    SensorMeasurement deserialize(SensorMessage sensorMessage);
}
