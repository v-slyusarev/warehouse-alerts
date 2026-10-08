package com.github.vslyusarev.warehousealerts.warehouse.infrastructure.serialization;

import com.github.vslyusarev.warehousealerts.shared.WarehouseMessageKey;
import com.github.vslyusarev.warehousealerts.shared.WarehouseMessagePayload;
import com.github.vslyusarev.warehousealerts.warehouse.infrastructure.config.ConfigProvider;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorMeasurement;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorType;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.output.CorrelationMetadata;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.output.OutboundMessage;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.output.OutboundMessageSerializer;

public class ProtobufOutboundMessageSerializer implements OutboundMessageSerializer {
    private final ConfigProvider configProvider;
    private final ProtobufMapper protobufMapper = new ProtobufMapper();

    public ProtobufOutboundMessageSerializer(ConfigProvider configProvider) {
        this.configProvider = configProvider;
    }

    @Override
    public OutboundMessage serialize(SensorMeasurement sensorMeasurement) {
        WarehouseMessageKey key = toWarehouseMessageKey(sensorMeasurement);
        WarehouseMessagePayload payload = toWarehouseMessagePayload(sensorMeasurement);
        CorrelationMetadata correlationMetadata = new CorrelationMetadata(sensorMeasurement);

        return new OutboundMessage(key.toByteArray(), payload.toByteArray(), correlationMetadata);
    }

    private WarehouseMessageKey toWarehouseMessageKey(SensorMeasurement sensorMeasurement) {
        return WarehouseMessageKey.newBuilder()
                .setWarehouseId(configProvider.getWarehouseId())
                .setSensorId(sensorMeasurement.sensorId())
                .build();
    }

    private WarehouseMessagePayload toWarehouseMessagePayload(SensorMeasurement sensorMeasurement) {
       return WarehouseMessagePayload.newBuilder()
                .setWarehouseId(configProvider.getWarehouseId())
                .setTimestamp(protobufMapper.toProtobufTimestamp(sensorMeasurement.timestamp()))
                .setSensorType(toProtobufSensorType(sensorMeasurement.sensorType()))
                .setSensorId(sensorMeasurement.sensorId())
                .setValue(sensorMeasurement.value())
                .build();
    }

    private com.github.vslyusarev.warehousealerts.shared.SensorType toProtobufSensorType(SensorType sensorType) {
        return switch (sensorType) {
            case TEMPERATURE -> com.github.vslyusarev.warehousealerts.shared.SensorType.TEMPERATURE;
            case HUMIDITY -> com.github.vslyusarev.warehousealerts.shared.SensorType.HUMIDITY;
        };
    }
}
