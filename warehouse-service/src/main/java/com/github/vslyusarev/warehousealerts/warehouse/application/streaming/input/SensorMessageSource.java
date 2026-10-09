package com.github.vslyusarev.warehousealerts.warehouse.application.streaming.input;

import reactor.core.publisher.Flux;

public interface SensorMessageSource {
    Flux<SensorMessage> getMessages();
}
