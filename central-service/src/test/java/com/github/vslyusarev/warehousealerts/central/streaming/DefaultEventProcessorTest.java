package com.github.vslyusarev.warehousealerts.central.streaming;

import com.github.vslyusarev.warehousealerts.central.application.filters.EventFilter;
import com.github.vslyusarev.warehousealerts.central.application.model.Alert;
import com.github.vslyusarev.warehousealerts.central.application.thresholds.ThresholdAlertService;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorType;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class EventProcessorTest {
    private final static SensorEvent sensorEvent = new SensorEvent("w1", "t1", SensorType.TEMPERATURE, 0, Instant.EPOCH);
    private final static Alert alert = new Alert("w1", "t1", Instant.EPOCH, 0, 0);
    private final static AlertEmitter successAlertEmitter = alert -> Mono.empty();
    private final static AlertEmitter failureAlertEmitter = alert -> Mono.error(new Throwable());
    private final static ThresholdAlertService withAlert = event -> Optional.of(alert);
    private final static ThresholdAlertService withoutAlert = event -> Optional.empty();
    private final static EventFilter eventDropped = event -> false;
    private final static EventFilter eventAccepted = event -> true;
    private final static Retry retryPolicy = Retry.max(1);

    @Test
    void testDefaultEventProcessorWhenAlertIsCreatedAndNoError() {
        final EventProcessor eventProcessor = new DefaultEventProcessor(withAlert, successAlertEmitter, retryPolicy);
        assertExecutesOnSuccess(eventProcessor);
        assertConsumeEventSucceeds(eventProcessor);
    }

    @Test
    void testDefaultEventProcessorWhenEmitterFails() {
        final EventProcessor eventProcessor = new DefaultEventProcessor(withAlert, failureAlertEmitter, retryPolicy);
        assertNotExecutesOnSuccess(eventProcessor);
        assertConsumeEventFails(eventProcessor);
    }

    @Test
    void testDefaultEventProcessorWhenAlertIsNotCreated() {
        final EventProcessor eventProcessor = new DefaultEventProcessor(withoutAlert, failureAlertEmitter, retryPolicy);
        assertExecutesOnSuccess(eventProcessor);
        assertConsumeEventSucceeds(eventProcessor);
    }

    @Test
    void testFilteringEventProcessorWhenEventIsDropped() {
        final EventProcessor eventProcessor = new FilteringEventProcessor(new DefaultEventProcessor(withAlert, failureAlertEmitter, retryPolicy), eventDropped);
        assertExecutesOnSuccess(eventProcessor);
        assertConsumeEventSucceeds(eventProcessor);
    }

    @Test
    void testFilteringEventProcessorWhenAlertIsCreatedAndNoError() {
        final EventProcessor eventProcessor = new FilteringEventProcessor(new DefaultEventProcessor(withAlert, successAlertEmitter, retryPolicy), eventAccepted);
        assertExecutesOnSuccess(eventProcessor);
        assertConsumeEventSucceeds(eventProcessor);
    }

    @Test
    void testFilteringEventProcessorWhenEmitterFails() {
        final EventProcessor eventProcessor = new FilteringEventProcessor(new DefaultEventProcessor(withAlert, failureAlertEmitter, retryPolicy), eventAccepted);
        assertNotExecutesOnSuccess(eventProcessor);
        assertConsumeEventFails(eventProcessor);
    }

    @Test
    void testFilteringEventProcessorWhenAlertIsNotCreated() {
        final EventProcessor eventProcessor = new FilteringEventProcessor(new DefaultEventProcessor(withoutAlert, failureAlertEmitter, retryPolicy), eventAccepted);
        assertExecutesOnSuccess(eventProcessor);
        assertConsumeEventSucceeds(eventProcessor);
    }

    @Test
    void testRetries() {
        AtomicInteger retryCounter = new AtomicInteger();
        final AlertEmitter alertEmitter = alert -> {
            retryCounter.incrementAndGet();
            return Mono.error(new Throwable());
        };
        final int expectedRetryCount = 10;
        final EventProcessor eventProcessor = new DefaultEventProcessor(withAlert, alertEmitter, Retry.max(expectedRetryCount));
        try {
            eventProcessor.consumeEvent(sensorEvent, () -> {}).block();
        } catch (IllegalStateException e) {
            // suppress the retry exhausted exception
        }
        assertEquals(expectedRetryCount + 1, retryCounter.get());

    }


    private void assertExecutesOnSuccess(EventProcessor eventProcessor) {
        testOnSuccessExecution(eventProcessor, true);
    }

    private void assertNotExecutesOnSuccess(EventProcessor eventProcessor) {
        testOnSuccessExecution(eventProcessor, false);
    }

    private void assertConsumeEventFails(EventProcessor eventProcessor) {
        assertThrows(Throwable.class, () -> eventProcessor.consumeEvent(sensorEvent, () -> {}).block());
    }

    private void assertConsumeEventSucceeds(EventProcessor eventProcessor) {
        assertDoesNotThrow(() -> eventProcessor.consumeEvent(sensorEvent, () -> {}).block());
    }

    private void testOnSuccessExecution(EventProcessor eventProcessor, boolean shouldExecuteOnSuccess) {
        AtomicBoolean onSuccessExecuted = new AtomicBoolean(false);
        try {
            eventProcessor.consumeEvent(sensorEvent, () -> onSuccessExecuted.set(true)).block();
        } catch (Throwable e) {
            // suppress
        }
        assertEquals(shouldExecuteOnSuccess, onSuccessExecuted.get());
    }
}