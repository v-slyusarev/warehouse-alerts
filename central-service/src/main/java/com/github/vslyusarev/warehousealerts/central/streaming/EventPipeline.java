package com.github.vslyusarev.warehousealerts.central.streaming;

public class EventPipeline {
    private final EventSource eventSource;
    private final EventProcessor eventProcessor;

    public EventPipeline(EventSource eventSource, DefaultEventProcessor eventProcessor) {
        this.eventSource = eventSource;
        this.eventProcessor = eventProcessor;
    }

    public void run() {
        eventSource.subscribe(eventProcessor);
    }
}
