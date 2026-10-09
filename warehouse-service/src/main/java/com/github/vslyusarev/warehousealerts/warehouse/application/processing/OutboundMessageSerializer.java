package com.github.vslyusarev.warehousealerts.warehouse.application.processing;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.output.OutboundMessage;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorEvent;

public interface OutboundMessageSerializer {
    OutboundMessage serialize(SensorEvent sensorEvent);
}
