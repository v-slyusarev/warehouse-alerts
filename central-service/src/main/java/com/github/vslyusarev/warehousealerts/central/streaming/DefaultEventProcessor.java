package com.github.vslyusarev.warehousealerts.central.streaming;

import com.github.vslyusarev.warehousealerts.central.application.model.Alert;
import com.github.vslyusarev.warehousealerts.central.application.thresholds.ThresholdAlertService;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.Optional;

public class DefaultEventProcessor implements EventProcessor {
    private final ThresholdAlertService thresholdAlertService;
    private final AlertEmitter alertEmitter;
    private final Retry retryPolicy;

    private static final Logger log = LoggerFactory.getLogger(DefaultEventProcessor.class);

    public DefaultEventProcessor(ThresholdAlertService thresholdAlertService, AlertEmitter alertEmitter, Retry retryPolicy) {
        this.thresholdAlertService = thresholdAlertService;
        this.alertEmitter = alertEmitter;
        this.retryPolicy = retryPolicy;
    }

    public DefaultEventProcessor(DefaultEventProcessor other) {
        this(other.thresholdAlertService, other.alertEmitter, other.retryPolicy);
    }

    @Override
    public Mono<Void> consumeEvent(SensorEvent sensorEvent, Runnable onSuccess) {
        Optional<Alert> optionalAlert = thresholdAlertService.process(sensorEvent);
        return Mono.defer(() -> optionalAlert
                        .map(Mono::just).orElseGet(Mono::empty)
                        .flatMap(alertEmitter::emit)
                )
                .doOnSuccess(result -> onSuccess.run())
                .doOnError(error -> log.warn("Failed to emit alert {}. Retry policy will be applied",
                        optionalAlert.map(Alert::toString).orElse("<unknown>")))
            .retryWhen(retryPolicy);
    }
}
