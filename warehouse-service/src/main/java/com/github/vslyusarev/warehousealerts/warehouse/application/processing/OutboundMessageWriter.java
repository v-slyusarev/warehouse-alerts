package com.github.vslyusarev.warehousealerts.warehouse.application.processing;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.output.OutboundMessage;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;

public interface OutboundMessageWriter {
    OutboundMessage serialize(SensorEvent sensorEvent);
}
