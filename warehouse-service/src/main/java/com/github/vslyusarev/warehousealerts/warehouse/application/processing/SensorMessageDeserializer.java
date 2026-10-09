package com.github.vslyusarev.warehousealerts.warehouse.application.processing;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.input.SensorMeasurement;

public interface SensorMessageDeserializer {
    SensorMeasurement deserialize(byte[] payload);
}
