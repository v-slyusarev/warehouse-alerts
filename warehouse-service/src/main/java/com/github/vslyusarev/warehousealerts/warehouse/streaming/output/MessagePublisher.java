package com.github.vslyusarev.warehousealerts.warehouse.streaming.output;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessagePublisher {
    Mono<Void> publish(Flux<OutboundMessage> messages);
}
