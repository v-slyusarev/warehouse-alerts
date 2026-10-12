package com.github.vslyusarev.warehousealerts.shared.serialization;

import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;

public interface WarehouseMessageSerializer {
    byte[] toWarehouseMessageKey(SensorEvent sensorEvent);

    byte[] toWarehouseMessagePayload(SensorEvent sensorEvent);
}
