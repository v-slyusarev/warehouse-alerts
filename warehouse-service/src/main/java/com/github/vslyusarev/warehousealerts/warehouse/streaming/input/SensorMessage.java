package com.github.vslyusarev.warehousealerts.warehouse.streaming.input;

import java.time.Instant;
import java.time.ZonedDateTime;

public record SensorMessage (
        String sender,
        Instant timestamp,
        byte[] payload
) {
}
