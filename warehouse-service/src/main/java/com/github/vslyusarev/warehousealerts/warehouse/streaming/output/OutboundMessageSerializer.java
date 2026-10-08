package com.github.vslyusarev.warehousealerts.warehouse.streaming.output;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorMeasurement;

public interface OutboundMessageSerializer {
    public OutboundMessage serialize(SensorMeasurement sensorMeasurement);
}
