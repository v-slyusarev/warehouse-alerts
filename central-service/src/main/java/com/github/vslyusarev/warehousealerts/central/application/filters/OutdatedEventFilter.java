package com.github.vslyusarev.warehousealerts.central.application.filters;

import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;

import java.time.Duration;

public class OutdatedEventFilter implements EventFilter {
    final Duration maxEventAge;

    public OutdatedEventFilter(EventFilterConfigProvider configProvider) {
        maxEventAge = configProvider.getMaxEventAge();
    }

    @Override
    public boolean accept(SensorEvent sensorEvent) {
        return sensorEvent.timestamp().isAfter(java.time.Instant.now().minus(maxEventAge));
    }
}
