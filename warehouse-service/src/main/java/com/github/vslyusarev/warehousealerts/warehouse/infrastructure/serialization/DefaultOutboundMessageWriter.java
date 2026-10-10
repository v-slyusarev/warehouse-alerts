package com.github.vslyusarev.warehousealerts.warehouse.infrastructure.serialization;

import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;
import com.github.vslyusarev.warehousealerts.shared.serialization.WarehouseMessageSerializer;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.output.CorrelationMetadata;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.output.OutboundMessage;
import com.github.vslyusarev.warehousealerts.warehouse.application.processing.OutboundMessageWriter;

public class DefaultOutboundMessageWriter implements OutboundMessageWriter {
    private final WarehouseMessageSerializer warehouseMessageSerializer;

    public DefaultOutboundMessageWriter(WarehouseMessageSerializer warehouseMessageSerializer) {
        this.warehouseMessageSerializer = warehouseMessageSerializer;
    }

    @Override
    public OutboundMessage serialize(SensorEvent sensorEvent) {
        final byte[] key = warehouseMessageSerializer.toWarehouseMessageKey(sensorEvent);
        final byte[] payload = warehouseMessageSerializer.toWarehouseMessagePayload(sensorEvent);
        CorrelationMetadata correlationMetadata = new CorrelationMetadata(sensorEvent);

        return new OutboundMessage(key, payload, correlationMetadata);
    }

}
