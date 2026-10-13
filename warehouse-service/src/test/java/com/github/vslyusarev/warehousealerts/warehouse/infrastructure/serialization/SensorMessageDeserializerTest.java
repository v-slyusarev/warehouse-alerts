package com.github.vslyusarev.warehousealerts.warehouse.infrastructure.serialization;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.input.SensorMeasurement;
import com.github.vslyusarev.warehousealerts.warehouse.application.processing.SensorMessageDeserializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SensorMessageDeserializerTest {
    @Test
    void testHappyPath() {
        final String measurement = "sensor_id=t1; value=30";
        final SensorMessageDeserializer sensorMessageDeserializer = new DefaultSensorMessageDeserializer();
        final SensorMeasurement result = sensorMessageDeserializer.deserialize(measurement.getBytes());
        assertEquals("t1", result.sensorId());
        assertEquals(30, result.value());
    }

    @Test
    void testNegativeValue() {
        final String measurement = "sensor_id=t1; value=-1";
        final SensorMessageDeserializer sensorMessageDeserializer = new DefaultSensorMessageDeserializer();
        final SensorMeasurement result = sensorMessageDeserializer.deserialize(measurement.getBytes());
        assertEquals(-1, result.value());
    }

    @Test
    void testZeroValue() {
        final String measurement = "sensor_id=t1; value=0";
        final SensorMessageDeserializer sensorMessageDeserializer = new DefaultSensorMessageDeserializer();
        final SensorMeasurement result = sensorMessageDeserializer.deserialize(measurement.getBytes());
        assertEquals(0, result.value());
    }

    @Test
    void testLargeValue() {
        final String measurement = "sensor_id=t1; value=1000000";
        final SensorMessageDeserializer sensorMessageDeserializer = new DefaultSensorMessageDeserializer();
        final SensorMeasurement result = sensorMessageDeserializer.deserialize(measurement.getBytes());
        assertEquals(1000000, result.value());
    }

    @Test
    void testLargeNegativeValue() {
        final String measurement = "sensor_id=t1; value=-1000000";
        final SensorMessageDeserializer sensorMessageDeserializer = new DefaultSensorMessageDeserializer();
        final SensorMeasurement result = sensorMessageDeserializer.deserialize(measurement.getBytes());
        assertEquals(-1000000, result.value());
    }

    @Test
    void testLongMessageFails() {
        final String prefix = "sensor_id=";
        final String suffix = "; value=30";
        final int maxTotalLength = 101;
        final String longSensorId = "t".repeat(maxTotalLength - prefix.length() - suffix.length());
        final String measurement = prefix + longSensorId + suffix;
        final SensorMessageDeserializer sensorMessageDeserializer = new DefaultSensorMessageDeserializer();
        assertThrows(SensorMessageFormatException.class, () -> sensorMessageDeserializer.deserialize(measurement.getBytes()));
    }

    @Test
    void testBlankSensorIdFails() {
        final String measurement = "sensor_id=; value=30";
        final SensorMessageDeserializer sensorMessageDeserializer = new DefaultSensorMessageDeserializer();
        assertThrows(SensorMessageFormatException.class, () -> sensorMessageDeserializer.deserialize(measurement.getBytes()));
    }

    @Test
    void testBlankValueFails() {
        final String measurement = "sensor_id=t1; value=";
        final SensorMessageDeserializer sensorMessageDeserializer = new DefaultSensorMessageDeserializer();
        assertThrows(SensorMessageFormatException.class, () -> sensorMessageDeserializer.deserialize(measurement.getBytes()));
    }

    @Test
    void testInvalidNumberFormatFails() {
        final String measurement = "sensor_id=t1; value=30.0";
        final SensorMessageDeserializer sensorMessageDeserializer = new DefaultSensorMessageDeserializer();
        assertThrows(SensorMessageFormatException.class, () -> sensorMessageDeserializer.deserialize(measurement.getBytes()));
    }

    @Test
    void testMissingDelimiterFails() {
        final String measurement = "sensor_id=t1 value=30.0";
        final SensorMessageDeserializer sensorMessageDeserializer = new DefaultSensorMessageDeserializer();
        assertThrows(SensorMessageFormatException.class, () -> sensorMessageDeserializer.deserialize(measurement.getBytes()));
    }

    @Test
    void testWrongOrderFails() {
        final String measurement = "value=30; sensor_id=t1";
        final SensorMessageDeserializer sensorMessageDeserializer = new DefaultSensorMessageDeserializer();
        assertThrows(SensorMessageFormatException.class, () -> sensorMessageDeserializer.deserialize(measurement.getBytes()));
    }

    @Test
    void testWrongKeywordFails() {
        final String measurement = "sensorId=t1; value=30";
        final SensorMessageDeserializer sensorMessageDeserializer = new DefaultSensorMessageDeserializer();
        assertThrows(SensorMessageFormatException.class, () -> sensorMessageDeserializer.deserialize(measurement.getBytes()));
    }

}