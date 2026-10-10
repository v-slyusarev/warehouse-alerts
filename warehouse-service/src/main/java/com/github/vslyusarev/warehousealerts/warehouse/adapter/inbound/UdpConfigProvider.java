package com.github.vslyusarev.warehousealerts.warehouse.adapter.inbound;

import com.github.vslyusarev.warehousealerts.shared.contracts.SensorType;

public interface UdpConfigProvider {
    int getUdpPort(SensorType sensorType);
}
