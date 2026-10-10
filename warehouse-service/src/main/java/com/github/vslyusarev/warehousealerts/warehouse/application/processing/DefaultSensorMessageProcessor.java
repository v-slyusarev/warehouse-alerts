package com.github.vslyusarev.warehousealerts.warehouse.application.processing;

import com.github.vslyusarev.warehousealerts.warehouse.application.GeneralConfigProvider;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.input.SensorMeasurement;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.input.SensorMessage;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.output.OutboundMessage;

public class DefaultSensorMessageProcessor implements SensorMessageProcessor {
    private final SensorMessageDeserializer sensorMessageDeserializer;
    private final OutboundMessageWriter outboundMessageSerializer;
    private final String warehouseId;

    public DefaultSensorMessageProcessor(SensorMessageDeserializer sensorMessageDeserializer, OutboundMessageWriter outboundMessageSerializer, GeneralConfigProvider configProvider) {
        this.sensorMessageDeserializer = sensorMessageDeserializer;
        this.outboundMessageSerializer = outboundMessageSerializer;
        this.warehouseId = configProvider.getWarehouseId();
    }

    @Override
    public OutboundMessage process(SensorMessage sensorMessage) {
        final SensorMeasurement sensorMeasurement = sensorMessageDeserializer.deserialize(sensorMessage.payload());
        final SensorEvent sensorEvent = new SensorEvent(
                warehouseId,
                sensorMeasurement.sensorId(),
                sensorMessage.sensorType(),
                sensorMeasurement.value(),
                sensorMessage.wallTimestamp());
        return outboundMessageSerializer.serialize(sensorEvent);
    }
}
