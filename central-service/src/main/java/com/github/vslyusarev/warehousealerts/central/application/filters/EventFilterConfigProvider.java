package com.github.vslyusarev.warehousealerts.central.application.filters;

import java.time.Duration;

public interface EventFilterConfigProvider {
    boolean isFilterEnabled();
    Duration getMaxEventAge();
}
