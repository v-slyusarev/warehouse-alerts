package com.github.vslyusarev.warehousealerts.central.infrastructure;

import com.github.vslyusarev.warehousealerts.central.application.filters.TimeProvider;

import java.time.Instant;

public class SystemTimeProvider implements TimeProvider {

    @Override
    public Instant getCurrentInstant() {
        return Instant.now();
    }
}
