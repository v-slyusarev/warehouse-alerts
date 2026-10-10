package com.github.vslyusarev.warehousealerts.central.application.state;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySensorsState implements SensorsState {
    private final ConcurrentHashMap<GlobalSensorId, Integer> stateMap = new ConcurrentHashMap<>();
    @Override
    public Optional<Integer> getAndUpdateState(String warehouseId, String sensorId, int newValue) {
        return Optional.ofNullable(stateMap.put(new GlobalSensorId(warehouseId, sensorId), newValue));
    }
}
