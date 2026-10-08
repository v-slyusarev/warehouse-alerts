package com.github.vslyusarev.warehousealerts.warehouse.infrastructure.serialization;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorMeasurement;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorType;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.input.SensorMessage;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.input.SensorMessageDeserializer;

public class DefaultSensorMessageDeserializer implements SensorMessageDeserializer {
    @Override
    public SensorMeasurement deserialize(SensorType sensorType, SensorMessage sensorMessage) {
        String messageText = new String(sensorMessage.payload());
        if(messageText.length() > 100) {
            throw new IllegalArgumentException("Message is too long: " + messageText.length());
        }
        int sensorIdStart = messageText.indexOf("sensor_id=") + 10;
        int sensorIdEnd = messageText.indexOf(';', sensorIdStart);

        int valueStart = messageText.indexOf("value=", sensorIdEnd) + 6;

        String sensorId = messageText.substring(sensorIdStart, sensorIdEnd);
        int value = Integer.parseInt(messageText.substring(valueStart).trim());

        return new SensorMeasurement(sensorId, sensorType, value, sensorMessage.timestamp());
    }
}
