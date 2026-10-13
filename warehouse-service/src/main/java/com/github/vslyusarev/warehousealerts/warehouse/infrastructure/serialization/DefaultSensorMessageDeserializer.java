package com.github.vslyusarev.warehousealerts.warehouse.infrastructure.serialization;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.input.SensorMeasurement;
import com.github.vslyusarev.warehousealerts.warehouse.application.processing.SensorMessageDeserializer;

public class DefaultSensorMessageDeserializer implements SensorMessageDeserializer {
    private static final String SENSOR_IR_LABEL = "sensor_id=";
    private static final int SENSOR_IR_LABEL_LENGTH = SENSOR_IR_LABEL.length();
    private static final char SEPARATOR = ';';
    private static final String VALUE_LABEL = "value=";
    private static final int VALUE_LABEL_LENGTH = VALUE_LABEL.length();

    @Override
    public SensorMeasurement deserialize(byte[] payload) {
        String messageText = new String(payload);
        if(messageText.length() > 100) {
            throw new SensorMessageFormatException("The message is too long");
        }
        int sensorIdLabelStart = messageText.indexOf(SENSOR_IR_LABEL);
        int sensorIdStart = sensorIdLabelStart + SENSOR_IR_LABEL_LENGTH;
        int sensorIdEnd = messageText.indexOf(SEPARATOR, sensorIdStart);
        int valueLabelStart = messageText.indexOf(VALUE_LABEL, sensorIdEnd);
        int valueStart = valueLabelStart + VALUE_LABEL_LENGTH;

        if(sensorIdLabelStart == -1 || sensorIdEnd == -1 || valueLabelStart == -1) {
            throw new SensorMessageFormatException("Invalid measurement syntax");
        }

        String sensorId = messageText.substring(sensorIdStart, sensorIdEnd);

        if(sensorId.isEmpty()) {
            throw new SensorMessageFormatException("sensor_id cannot be empty");
        }
        int value;
        try {
            value = Integer.parseInt(messageText.substring(valueStart).trim());
        } catch (NumberFormatException e) {
            throw new SensorMessageFormatException("Invalid value format: %s".formatted(messageText.substring(valueStart)));
        }

        return new SensorMeasurement(sensorId, value);
    }

}
