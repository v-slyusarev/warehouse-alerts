package com.github.vslyusarev.warehousealerts.warehouse.application.streaming.output;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessagePublisher {
    Mono<Void> publish(Flux<OutboundMessage> messages);
}
