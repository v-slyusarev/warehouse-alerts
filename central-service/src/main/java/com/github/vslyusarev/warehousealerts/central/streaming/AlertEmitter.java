package com.github.vslyusarev.warehousealerts.central.streaming;

import com.github.vslyusarev.warehousealerts.central.application.model.Alert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AlertEmitter {
    Mono<Void> emit(Alert alert);
}
