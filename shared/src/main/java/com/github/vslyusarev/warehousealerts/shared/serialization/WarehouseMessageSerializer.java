package com.github.vslyusarev.warehousealerts.shared.serialization;

import com.github.vslyusarev.warehousealerts.shared.protobuf.WarehouseMessageKey;
import com.github.vslyusarev.warehousealerts.shared.protobuf.WarehouseMessagePayload;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorType;
import com.google.protobuf.util.Timestamps;

public class WarehouseMessageSerializer {
    public byte[] toWarehouseMessageKey(SensorEvent sensorEvent) {
        return WarehouseMessageKey.newBuilder()
                .setWarehouseId(sensorEvent.warehouseId())
                .setSensorId(sensorEvent.sensorId())
                .build()
                .toByteArray();
    }

    public byte[] toWarehouseMessagePayload(SensorEvent sensorEvent) {
       return WarehouseMessagePayload.newBuilder()
                .setWarehouseId(sensorEvent.warehouseId())
                .setTimestamp(Timestamps.fromMillis(sensorEvent.timestamp().toEpochMilli()))
                .setSensorType(toProtobufSensorType(sensorEvent.sensorType()))
                .setSensorId(sensorEvent.sensorId())
                .setValue(sensorEvent.value())
                .build()
                .toByteArray();
    }

    private com.github.vslyusarev.warehousealerts.shared.protobuf.SensorType toProtobufSensorType(SensorType sensorType) {
        return switch (sensorType) {
            case TEMPERATURE -> com.github.vslyusarev.warehousealerts.shared.protobuf.SensorType.TEMPERATURE;
            case HUMIDITY -> com.github.vslyusarev.warehousealerts.shared.protobuf.SensorType.HUMIDITY;
        };
    }
}
