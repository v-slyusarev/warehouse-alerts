package com.github.vslyusarev.warehousealerts.warehouse.application.streaming.input;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorType;

import java.net.SocketAddress;

public record SensorMessage(
        SocketAddress sender,
        long systemTimestampNanos,
        SensorType sensorType,
        byte[] payload
) {
}
