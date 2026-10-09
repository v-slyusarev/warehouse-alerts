package com.github.vslyusarev.warehousealerts.warehouse.application.processing;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.input.SensorMessage;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.output.OutboundMessage;

public interface SensorMessageProcessor {
    OutboundMessage process(SensorMessage sensorMessage);
}
