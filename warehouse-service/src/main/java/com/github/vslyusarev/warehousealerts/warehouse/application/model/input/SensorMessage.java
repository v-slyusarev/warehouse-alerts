package com.github.vslyusarev.warehousealerts.warehouse.application.model.input;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorType;

import java.net.SocketAddress;
import java.time.Instant;

public record SensorMessage(
        SocketAddress sender,
        long monotonicTimestamp,
        Instant wallTimestamp,
        SensorType sensorType,
        byte[] payload
) {
}
