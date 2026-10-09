package com.github.vslyusarev.warehousealerts.warehouse.infrastructure.serialization;

import com.github.vslyusarev.warehousealerts.shared.WarehouseMessageKey;
import com.github.vslyusarev.warehousealerts.shared.WarehouseMessagePayload;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorEvent;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorType;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.output.CorrelationMetadata;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.output.OutboundMessage;
import com.github.vslyusarev.warehousealerts.warehouse.application.processing.OutboundMessageSerializer;
import com.google.protobuf.util.Timestamps;

public class ProtobufOutboundMessageSerializer implements OutboundMessageSerializer {
    @Override
    public OutboundMessage serialize(SensorEvent sensorEvent) {
        WarehouseMessageKey key = toWarehouseMessageKey(sensorEvent);
        WarehouseMessagePayload payload = toWarehouseMessagePayload(sensorEvent);
        CorrelationMetadata correlationMetadata = new CorrelationMetadata(sensorEvent);

        return new OutboundMessage(key.toByteArray(), payload.toByteArray(), correlationMetadata);
    }

    private WarehouseMessageKey toWarehouseMessageKey(SensorEvent sensorEvent) {
        return WarehouseMessageKey.newBuilder()
                .setWarehouseId(sensorEvent.warehouseId())
                .setSensorId(sensorEvent.sensorId())
                .build();
    }

    private WarehouseMessagePayload toWarehouseMessagePayload(SensorEvent sensorEvent) {
       return WarehouseMessagePayload.newBuilder()
                .setWarehouseId(sensorEvent.warehouseId())
                .setTimestamp(Timestamps.fromMillis(sensorEvent.timestamp().toEpochMilli()))
                .setSensorType(toProtobufSensorType(sensorEvent.sensorType()))
                .setSensorId(sensorEvent.sensorId())
                .setValue(sensorEvent.value())
                .build();
    }

    private com.github.vslyusarev.warehousealerts.shared.SensorType toProtobufSensorType(SensorType sensorType) {
        return switch (sensorType) {
            case TEMPERATURE -> com.github.vslyusarev.warehousealerts.shared.SensorType.TEMPERATURE;
            case HUMIDITY -> com.github.vslyusarev.warehousealerts.shared.SensorType.HUMIDITY;
        };
    }
}
