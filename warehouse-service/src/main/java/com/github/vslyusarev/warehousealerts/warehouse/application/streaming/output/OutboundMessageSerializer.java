package com.github.vslyusarev.warehousealerts.warehouse.application.streaming.output;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorMeasurement;

public interface OutboundMessageSerializer {
    OutboundMessage serialize(SensorMeasurement sensorMeasurement);
}
