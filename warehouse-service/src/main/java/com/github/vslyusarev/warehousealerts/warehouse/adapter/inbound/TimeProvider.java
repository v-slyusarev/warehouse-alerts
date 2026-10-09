package com.github.vslyusarev.warehousealerts.warehouse.adapter.inbound;

import java.time.Instant;

public interface TimeProvider {
    long getCurrentSystemTimeNano();
    Instant getCurrentInstant();
}
