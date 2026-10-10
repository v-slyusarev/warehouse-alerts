package com.github.vslyusarev.warehousealerts.central.adapter.outbound;

import com.github.vslyusarev.warehousealerts.central.streaming.AlertEmitter;
import com.github.vslyusarev.warehousealerts.central.application.model.Alert;
import reactor.core.publisher.Mono;

public class ConsoleAlertEmitter implements AlertEmitter {
    @Override
    public Mono<Void> emit(Alert alert) {
        return Mono.fromCallable(() -> {
            System.out.printf(
                    "ALERT: Threshold crossed for warehouse %s, sensor %s. Threshold: %d, actual value: %d%n",
                    alert.warehouseId(),
                    alert.sensorId(),
                    alert.threshold(),
                    alert.actualValue());
            return null;
        });
    }
}
