package com.github.vslyusarev.warehousealerts.warehouse.infrastructure.serialization;

import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.input.SensorMessage;

public class SensorMessageFormatException extends RuntimeException {
    public SensorMessageFormatException(SensorMessage sensorMessage) {
        super("Received illegal sensor message format from %s".formatted(sensorMessage.sender()));
    }
}
