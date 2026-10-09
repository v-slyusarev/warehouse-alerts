package com.github.vslyusarev.warehousealerts.warehouse.application.streaming.input;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorType;

import java.util.Arrays;
import java.util.List;

public interface SensorMessageSourceFactory {
    SensorMessageSource createForSensorType(SensorType sensorType);

    default List<SensorMessageSource> createSources() {
        return Arrays.stream(SensorType.values())
                .map(this::createForSensorType)
                .toList();
    }
}
