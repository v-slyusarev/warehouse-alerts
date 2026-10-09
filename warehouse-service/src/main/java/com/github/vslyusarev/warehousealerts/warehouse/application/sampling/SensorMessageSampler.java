package com.github.vslyusarev.warehousealerts.warehouse.application.sampling;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.input.SensorMessage;

public interface SensorMessageSampler {
    boolean accept(SensorMessage sensorMessage);
}
