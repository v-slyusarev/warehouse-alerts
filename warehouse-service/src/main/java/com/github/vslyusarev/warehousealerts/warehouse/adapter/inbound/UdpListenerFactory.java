package com.github.vslyusarev.warehousealerts.warehouse.adapter.inbound;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorType;
import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.input.SensorMessageSource;
import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.input.SensorMessageSourceFactory;

public class UdpListenerFactory implements SensorMessageSourceFactory {
    private final UdpConfigProvider configProvider;
    private final TimeProvider timeProvider;

    public UdpListenerFactory(UdpConfigProvider configProvider, TimeProvider timeProvider) {
        this.configProvider = configProvider;
        this.timeProvider = timeProvider;
    }

    @Override
    public SensorMessageSource createForSensorType(SensorType sensorType) {
        return new UdpListener(configProvider.getUdpPort(sensorType), timeProvider, sensorType);
    }
}
