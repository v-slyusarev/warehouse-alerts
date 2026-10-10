package com.github.vslyusarev.warehousealerts.central.streaming;

import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

public interface EventSource {
    Disposable subscribe(EventProcessor eventProcessor);
}
