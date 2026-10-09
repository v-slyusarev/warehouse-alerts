package com.github.vslyusarev.warehousealerts.warehouse.application.streaming;

import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.input.SensorMessage;

public interface SensorMessageSampler {
    boolean accept(SensorMessage sensorMessage);
}
