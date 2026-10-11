package com.github.vslyusarev.warehousealerts.central.streaming;

import com.github.vslyusarev.warehousealerts.central.application.filters.EventFilter;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;
import reactor.core.publisher.Mono;

public class FilteringEventProcessor extends DefaultEventProcessor {
    private final EventFilter eventFilter;

    public FilteringEventProcessor(DefaultEventProcessor eventProcessor, EventFilter eventFilter) {
        super(eventProcessor);
        this.eventFilter = eventFilter;
    }

    @Override
    public Mono<Void> consumeEvent(SensorEvent sensorEvent, Runnable onSuccess) {
        if (eventFilter.filter(sensorEvent)) {
            return super.consumeEvent(sensorEvent, onSuccess);
        }
        else {
            onSuccess.run();
            return Mono.empty();
        }
    }
}
