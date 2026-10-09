package com.github.vslyusarev.warehousealerts.warehouse.adapter.inbound;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorType;

public interface UdpConfigProvider {
    int getUdpPort(SensorType sensorType);
}
