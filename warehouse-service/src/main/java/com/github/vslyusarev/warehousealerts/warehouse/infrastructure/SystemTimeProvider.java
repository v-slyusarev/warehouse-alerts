package com.github.vslyusarev.warehousealerts.warehouse.infrastructure;

import com.github.vslyusarev.warehousealerts.warehouse.adapter.inbound.TimeProvider;

import java.time.Instant;

public class SystemTimeProvider implements TimeProvider {
    @Override
    public long getCurrentSystemTimeNano() {
        return System.nanoTime();
    }

    @Override
    public Instant getCurrentInstant() {
        return Instant.now();
    }
}
