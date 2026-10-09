package com.github.vslyusarev.warehousealerts.warehouse.infrastructure.serialization;

import com.github.vslyusarev.warehousealerts.warehouse.adapter.inbound.TimeProvider;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorMeasurement;
import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.input.SensorMessage;
import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.input.SensorMessageDeserializer;

public class DefaultSensorMessageDeserializer implements SensorMessageDeserializer {
    private final TimeProvider timeProvider;
    private static final String SENSOR_IR_LABEL = "sensor_id=";
    private static final int SENSOR_IR_LABEL_LENGTH = SENSOR_IR_LABEL.length();
    private static final char SEPARATOR = ';';
    private static final String VALUE_LABEL = "value=";
    private static final int VALUE_LABEL_LENGTH = VALUE_LABEL.length();

    public DefaultSensorMessageDeserializer(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @Override
    public SensorMeasurement deserialize(SensorMessage sensorMessage) {
        String messageText = new String(sensorMessage.payload());
        if(messageText.length() > 100) {
            throw new SensorMessageFormatException(sensorMessage);
        }
        int sensorIdLabelStart = messageText.indexOf(SENSOR_IR_LABEL);
        int sensorIdStart = sensorIdLabelStart + SENSOR_IR_LABEL_LENGTH;
        int sensorIdEnd = messageText.indexOf(SEPARATOR, sensorIdStart);
        int valueLabelStart = messageText.indexOf(VALUE_LABEL, sensorIdEnd);
        int valueStart = valueLabelStart + VALUE_LABEL_LENGTH;

        if(sensorIdLabelStart == -1 || sensorIdEnd == -1 || valueLabelStart == -1) {
            throw new SensorMessageFormatException(sensorMessage);
        }

        String sensorId = messageText.substring(sensorIdStart, sensorIdEnd);
        int value = Integer.parseInt(messageText.substring(valueStart).trim());

        return new SensorMeasurement(sensorId, sensorMessage.sensorType(), value, timeProvider.getCurrentInstant());
    }

}
