package com.github.vslyusarev.warehousealerts.central.streaming;

import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;
import reactor.core.publisher.Mono;

public interface EventProcessor {
    Mono<Void> consumeEvent(SensorEvent sensorEvent, Runnable onSuccess);
}
