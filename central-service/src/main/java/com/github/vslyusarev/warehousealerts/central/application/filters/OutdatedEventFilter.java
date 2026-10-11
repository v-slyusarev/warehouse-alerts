package com.github.vslyusarev.warehousealerts.central.application.filters;

import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;

import java.time.Duration;

public class OutdatedEventFilter implements EventFilter {
    final TimeProvider timeProvider;
    final Duration maxEventAge;

    public OutdatedEventFilter(TimeProvider timeProvider, EventFilterConfigProvider configProvider) {
        this.timeProvider = timeProvider;
        maxEventAge = configProvider.getMaxEventAge();
        if(maxEventAge.isNegative() || maxEventAge.isZero()) {
            throw new IllegalArgumentException("maxEventAge must be positive");
        }
    }

    @Override
    public boolean filter(SensorEvent sensorEvent) {
        return !sensorEvent.timestamp().isBefore(timeProvider.getCurrentInstant().minus(maxEventAge));
    }
}
