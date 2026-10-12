package com.github.vslyusarev.warehousealerts.shared.serialization;

import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;

public interface WarehouseMessageDeserializer {
    SensorEvent toSensorEvent(byte[] messageBytes);
}
