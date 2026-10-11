package com.github.vslyusarev.warehousealerts.central.application.filters;

import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorType;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class OutdatedEventFilterTest {
    private static final Duration MAX_EVENT_AGE = Duration.ofMinutes(5);
    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final Instant CUTOFF = NOW.minus(MAX_EVENT_AGE);
    private static final Instant BEFORE_CUTOFF = CUTOFF.minusNanos(1);
    private static final Instant AFTER_CUTOFF = CUTOFF.plusNanos(1);
    private static final TimeProvider timeProvider = () -> NOW;

    private static final EventFilterConfigProvider eventFilterConfigProvider = new EventFilterConfigProvider() {
        @Override
        public boolean isFilterEnabled() {
            return true;
        }

        @Override
        public java.time.Duration getMaxEventAge() {
            return MAX_EVENT_AGE;
        }
    };

    private SensorEvent getSensorEvent(Instant instant) {
        return new SensorEvent("warehouse1", "t1", SensorType.TEMPERATURE, 0, instant);
    }

    @Test
    void testFreshEventIsNotFiltered() {
        final OutdatedEventFilter eventFilter = new OutdatedEventFilter(timeProvider, eventFilterConfigProvider);
        assertTrue(eventFilter.filter(getSensorEvent(AFTER_CUTOFF)));
    }

    @Test
    void testStaleEventIsFiltered() {
        final OutdatedEventFilter eventFilter = new OutdatedEventFilter(timeProvider, eventFilterConfigProvider);
        assertFalse(eventFilter.filter(getSensorEvent(BEFORE_CUTOFF)));
    }

    @Test
    void testEventOfExactlyMaxAgeIsNotFiltered() {
        final OutdatedEventFilter eventFilter = new OutdatedEventFilter(timeProvider, eventFilterConfigProvider);
        assertTrue(eventFilter.filter(getSensorEvent(CUTOFF)));
    }

}