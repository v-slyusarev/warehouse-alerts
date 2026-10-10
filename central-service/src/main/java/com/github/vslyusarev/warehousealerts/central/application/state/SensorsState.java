package com.github.vslyusarev.warehousealerts.central.application.state;

import java.util.Optional;

public interface SensorsState {
    Optional<Integer> getAndUpdateState(String warehouseId, String sensorId, int newValue);
}
