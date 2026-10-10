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
    final static SensorEvent sensorEvent = new SensorEvent(
            "warehouse1",
            "t1",
            SensorType.TEMPERATURE,
            30,
            Instant.EPOCH
    );

    @Test
    void testAlertCreatedWhenNoStateAndThresholdCrossed() {
        final ThresholdAlertService thresholdAlertService = new DefaultThresholdAlertService(
                (warehouseId, sensorId, newValue) -> Optional.empty(),
                () -> Map.of(SensorType.TEMPERATURE, 25)
        );

        final Optional<Alert> optionalAlert = thresholdAlertService.process(sensorEvent);
        assertTrue(optionalAlert.isPresent());
        assertEquals("warehouse1", optionalAlert.get().warehouseId());
        assertEquals("t1", optionalAlert.get().sensorId());
        assertEquals(Instant.EPOCH, optionalAlert.get().timestamp());
        assertEquals(25, optionalAlert.get().threshold());
        assertEquals(30, optionalAlert.get().actualValue());
    }

    @Test
    void testAlertNotCreatedWhenValueExactlyAtThreshold() {
        final ThresholdAlertService thresholdAlertService = new DefaultThresholdAlertService(
                (warehouseId, sensorId, newValue) -> Optional.empty(),
                () -> Map.of(SensorType.TEMPERATURE, 30)
        );

        final Optional<Alert> optionalAlert = thresholdAlertService.process(sensorEvent);
        assertTrue(optionalAlert.isEmpty());
    }

    @Test
    void testAlertNotCreatedWhenStateIsAboveThreshold() {
        final ThresholdAlertService thresholdAlertService = new DefaultThresholdAlertService(
                (warehouseId, sensorId, newValue) -> Optional.of(30),
                () -> Map.of(SensorType.TEMPERATURE, 25)
        );

        final Optional<Alert> optionalAlert = thresholdAlertService.process(sensorEvent);
        assertTrue(optionalAlert.isEmpty());
    }

    @Test
    void testAlertNotCreatedWhenThresholdNotCrossed() {
        final ThresholdAlertService thresholdAlertService = new DefaultThresholdAlertService(
                (warehouseId, sensorId, newValue) -> Optional.empty(),
                () -> Map.of(SensorType.TEMPERATURE, 25)
        );

        final Optional<Alert> optionalAlert = thresholdAlertService.process(sensorEvent);
        assertTrue(optionalAlert.isEmpty());
    }

    @Test
    void testAlertNotCreatedWhenThresholdNotDefined() {
        final ThresholdAlertService thresholdAlertService = new DefaultThresholdAlertService(
                (warehouseId, sensorId, newValue) -> Optional.empty(),
                () -> Map.of(SensorType.TEMPERATURE, 25)
        );

        final Optional<Alert> optionalAlert = thresholdAlertService.process(sensorEvent);
        assertTrue(optionalAlert.isEmpty());
    }
}