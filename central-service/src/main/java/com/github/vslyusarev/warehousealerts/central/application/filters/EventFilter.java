package com.github.vslyusarev.warehousealerts.central.application.filters;

import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;

public interface EventFilter {
    boolean accept(SensorEvent sensorEvent);
}
