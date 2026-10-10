package com.github.vslyusarev.warehousealerts.shared.serialization;

import com.github.vslyusarev.warehousealerts.shared.protobuf.WarehouseMessageKey;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorType;
import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SerializationDeserializationTest {
    static final SensorEvent temperatureEvent = new SensorEvent(
            "warehouse1",
            "t1",
            SensorType.TEMPERATURE,
            30,
            Instant.EPOCH
    );

    static final SensorEvent humidityEvent = new SensorEvent(
            "warehouse1",
            "t1",
            SensorType.HUMIDITY,
            30,
            Instant.EPOCH
    );

    @Test
    void testPayloadSerializationDeserialization() {
        final WarehouseMessageSerializer serializer = new WarehouseMessageSerializer();
        final WarehouseMessageDeserializer deserializer = new WarehouseMessageDeserializer();

        for(var event: List.of(temperatureEvent, humidityEvent)) {
            final byte[] serializedMessage = serializer.toWarehouseMessagePayload(event);
            final SensorEvent deserializedMessage = deserializer.toSensorEvent(serializedMessage);
            assertEquals(event, deserializedMessage);
        }
    }

    @Test
    void testKeySerialization() {
        final WarehouseMessageSerializer serializer = new WarehouseMessageSerializer();

        final byte[] serializedMessageKey = serializer.toWarehouseMessageKey(humidityEvent);

        try {
            final WarehouseMessageKey warehouseMessageKey = WarehouseMessageKey.parseFrom(serializedMessageKey);
            assertEquals(humidityEvent.warehouseId(), warehouseMessageKey.getWarehouseId());
            assertEquals(humidityEvent.sensorId(), warehouseMessageKey.getSensorId());
        } catch (InvalidProtocolBufferException e) {
            fail("Key deserialization failed");
        }
    }

    @Test
    void testInvalidMessageDeserialization() {
        final WarehouseMessageDeserializer deserializer = new WarehouseMessageDeserializer();

        assertThrows(DeserializationException.class, () -> {
            deserializer.toSensorEvent("Invalid".getBytes());
        });
    }
}