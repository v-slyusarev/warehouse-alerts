package com.github.vslyusarev.warehousealerts.warehouse.streaming.input;

import reactor.core.publisher.Flux;

public interface SensorMessageSource {
    Flux<SensorMessage> getMessages();
}
