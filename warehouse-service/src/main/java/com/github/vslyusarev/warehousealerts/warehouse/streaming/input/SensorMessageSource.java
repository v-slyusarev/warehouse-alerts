package com.github.vslyusarev.warehousealerts.warehouse.streaming.input;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.input.SensorMessage;
import reactor.core.publisher.Flux;

public interface SensorMessageSource {
    Flux<SensorMessage> getMessages();
}
