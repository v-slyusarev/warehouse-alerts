package com.github.vslyusarev.warehousealerts.shared.serialization;

import com.github.vslyusarev.warehousealerts.shared.protobuf.WarehouseMessagePayload;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorType;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.Timestamps;

import java.time.Instant;

public class WarehouseMessageDeserializer {
    public SensorEvent toSensorEvent(byte[] messageBytes) {
        final WarehouseMessagePayload warehouseMessagePayload;
        try {
            warehouseMessagePayload = WarehouseMessagePayload.parseFrom(messageBytes);
        } catch (InvalidProtocolBufferException e) {
            throw new DeserializationException(e);
        }
        return new SensorEvent(
                warehouseMessagePayload.getWarehouseId(),
                warehouseMessagePayload.getSensorId(),
                toSensorType(warehouseMessagePayload.getSensorType()),
                warehouseMessagePayload.getValue(),
                Instant.ofEpochMilli(Timestamps.toMillis(warehouseMessagePayload.getTimestamp()))
        );
    }

    private SensorType toSensorType(com.github.vslyusarev.warehousealerts.shared.protobuf.SensorType sensorType) {
        return switch (sensorType) {
            case TEMPERATURE -> SensorType.TEMPERATURE;
            case HUMIDITY -> SensorType.HUMIDITY;
            case UNRECOGNIZED -> throw new IllegalArgumentException("Unrecognized sensor type: " + sensorType);
        };
    }

}
