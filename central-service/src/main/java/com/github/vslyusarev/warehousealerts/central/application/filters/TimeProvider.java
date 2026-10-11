package com.github.vslyusarev.warehousealerts.central.application.filters;

import java.time.Instant;

public interface TimeProvider {
    Instant getCurrentInstant();
}
