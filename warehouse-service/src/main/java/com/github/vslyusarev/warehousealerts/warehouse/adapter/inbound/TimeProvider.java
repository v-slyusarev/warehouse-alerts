package com.github.vslyusarev.warehousealerts.warehouse.adapter.inbound;

import java.time.Instant;

public interface TimeProvider {
    Instant getCurrentInstant();
}
