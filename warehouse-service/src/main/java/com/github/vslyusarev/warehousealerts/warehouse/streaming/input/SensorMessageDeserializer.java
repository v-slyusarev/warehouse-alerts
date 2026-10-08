package com.github.vslyusarev.warehousealerts.warehouse.streaming.input;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorMeasurement;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorType;

public interface SensorMessageDeserializer {
    public SensorMeasurement deserialize(SensorType sensorType, SensorMessage sensorMessage);
}
