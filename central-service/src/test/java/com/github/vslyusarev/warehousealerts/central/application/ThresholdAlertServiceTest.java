package com.github.vslyusarev.warehousealerts.central.application;

import com.github.vslyusarev.warehousealerts.central.application.model.Alert;
import com.github.vslyusarev.warehousealerts.central.application.thresholds.DefaultThresholdAlertService;
import com.github.vslyusarev.warehousealerts.central.application.thresholds.ThresholdAlertService;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThresholdAlertServiceTest {
    private static final int THRESHOLD = 35;
    private static final int VALUE_UNDER_THRESHOLD = THRESHOLD - 1;
    private static final int VALUE_ABOVE_THRESHOLD = THRESHOLD + 1;

    private static SensorEvent getTemperatureEvent(int temperature) {
        return new SensorEvent(
                "warehouse1",
                "t1",
                SensorType.TEMPERATURE,
                temperature,
                Instant.EPOCH
        );
    }

    @Test
    void testAlertCreatedWhenNoStateAndThresholdCrossed() {
        final ThresholdAlertService thresholdAlertService = new DefaultThresholdAlertService(
                (warehouseId, sensorId, newValue) -> Optional.empty(),
                () -> Map.of(SensorType.TEMPERATURE, THRESHOLD)
        );

        final Optional<Alert> optionalAlert = thresholdAlertService.process(getTemperatureEvent(VALUE_ABOVE_THRESHOLD));
        assertTrue(optionalAlert.isPresent());
        assertEquals("warehouse1", optionalAlert.get().warehouseId());
        assertEquals("t1", optionalAlert.get().sensorId());
        assertEquals(Instant.EPOCH, optionalAlert.get().timestamp());
        assertEquals(THRESHOLD, optionalAlert.get().threshold());
        assertEquals(VALUE_ABOVE_THRESHOLD, optionalAlert.get().actualValue());
    }

    @Test
    void testAlertNotCreatedWhenValueExactlyAtThreshold() {
        final ThresholdAlertService thresholdAlertService = new DefaultThresholdAlertService(
                (warehouseId, sensorId, newValue) -> Optional.empty(),
                () -> Map.of(SensorType.TEMPERATURE, THRESHOLD)
        );

        final Optional<Alert> optionalAlert = thresholdAlertService.process(getTemperatureEvent(THRESHOLD));
        assertTrue(optionalAlert.isEmpty());
    }

    @Test
    void testAlertNotCreatedWhenStateIsAboveThreshold() {
        final ThresholdAlertService thresholdAlertService = new DefaultThresholdAlertService(
                (warehouseId, sensorId, newValue) -> Optional.of(VALUE_ABOVE_THRESHOLD),
                () -> Map.of(SensorType.TEMPERATURE, THRESHOLD)
        );

        final Optional<Alert> optionalAlert = thresholdAlertService.process(getTemperatureEvent(VALUE_ABOVE_THRESHOLD));
        assertTrue(optionalAlert.isEmpty());
    }

    @Test
    void testAlertNotCreatedWhenThresholdNotCrossed() {
        final ThresholdAlertService thresholdAlertService = new DefaultThresholdAlertService(
                (warehouseId, sensorId, newValue) -> Optional.empty(),
                () -> Map.of(SensorType.TEMPERATURE, THRESHOLD)
        );

        final Optional<Alert> optionalAlert = thresholdAlertService.process(getTemperatureEvent(VALUE_UNDER_THRESHOLD));
        assertTrue(optionalAlert.isEmpty());
    }

    @Test
    void testAlertNotCreatedWhenThresholdNotDefined() {
        final ThresholdAlertService thresholdAlertService = new DefaultThresholdAlertService(
                (warehouseId, sensorId, newValue) -> Optional.empty(),
                () -> Map.of(SensorType.HUMIDITY, THRESHOLD)
        );

        final Optional<Alert> optionalAlert = thresholdAlertService.process(getTemperatureEvent(VALUE_ABOVE_THRESHOLD));
        assertTrue(optionalAlert.isEmpty());
    }
}