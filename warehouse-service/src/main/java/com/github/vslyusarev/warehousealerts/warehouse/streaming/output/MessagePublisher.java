package com.github.vslyusarev.warehousealerts.warehouse.streaming.output;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.output.OutboundMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessagePublisher {
    Mono<Void> publish(Flux<OutboundMessage> messages);
}
