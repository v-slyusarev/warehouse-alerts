package com.github.vslyusarev.warehousealerts.warehouse.infrastructure.serialization;

import com.google.protobuf.Timestamp;

import java.time.Instant;

public class ProtobufMapper {
    public Timestamp toProtobufTimestamp(Instant instant) {
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();

    }
}
