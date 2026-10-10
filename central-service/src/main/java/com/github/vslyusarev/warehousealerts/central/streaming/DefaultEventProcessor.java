package com.github.vslyusarev.warehousealerts.central.streaming;

import com.github.vslyusarev.warehousealerts.central.application.filters.EventFilter;
import com.github.vslyusarev.warehousealerts.central.application.thresholds.ThresholdAlertService;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;
import io.micrometer.common.lang.Nullable;
import reactor.core.publisher.Mono;

public class DefaultEventProcessor implements EventProcessor {
    private final ThresholdAlertService thresholdAlertService;
    private final AlertEmitter alertEmitter;

    private EventFilter eventFilter = (event) -> true;

    public DefaultEventProcessor(ThresholdAlertService thresholdAlertService, AlertEmitter alertEmitter) {
        this.thresholdAlertService = thresholdAlertService;
        this.alertEmitter = alertEmitter;
    }

    public void setEventFilter(EventFilter eventFilter) {
        this.eventFilter = eventFilter;
    }

    @Override
    public Mono<Void> consumeEvent(SensorEvent sensorEvent, Runnable onSuccess) {
        return Mono.just(sensorEvent)
                .filter(eventFilter::accept)
                .map(thresholdAlertService::process)
                .flatMap(optionalAlert -> optionalAlert.map(Mono::just).orElseGet(Mono::empty))
                .flatMap(alertEmitter::emit)
                .doOnSuccess(result -> onSuccess.run());
        // TODO error handling
    }
}
