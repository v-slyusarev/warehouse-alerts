package com.github.vslyusarev.warehousealerts.warehouse;

import com.github.vslyusarev.warehousealerts.warehouse.adapter.inbound.UdpListener;
import com.github.vslyusarev.warehousealerts.warehouse.adapter.outbound.KafkaPublisher;
import com.github.vslyusarev.warehousealerts.warehouse.infrastructure.config.ConfigProvider;
import com.github.vslyusarev.warehousealerts.warehouse.infrastructure.config.MockConfigProvider;
import com.github.vslyusarev.warehousealerts.warehouse.infrastructure.serialization.ProtobufOutboundMessageSerializer;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorType;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.input.SensorMessageDeserializer;
import com.github.vslyusarev.warehousealerts.warehouse.infrastructure.serialization.DefaultSensorMessageDeserializer;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.SensorMessagePipeline;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.output.MessagePublisher;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.output.OutboundMessageSerializer;

import java.util.EnumMap;
import java.util.Map;


public class Main {

    private static final Map<SensorType, Integer> sensorTypePorts = Map.of(
            SensorType.TEMPERATURE, 3344,
            SensorType.HUMIDITY, 3355
    ) ;

    public static void main(String[] args) {
        final Map<SensorType, UdpListener> udpListeners = new EnumMap<>(SensorType.class);
        for (Map.Entry<SensorType, Integer> entry : sensorTypePorts.entrySet()) {
            udpListeners.put(entry.getKey(), new UdpListener(entry.getValue()));
        }
        final ConfigProvider configProvider = new MockConfigProvider();
        final SensorMessageDeserializer sensorMessageDeserializer = new DefaultSensorMessageDeserializer();
        final OutboundMessageSerializer outboundMessageSerializer = new ProtobufOutboundMessageSerializer(configProvider);
        final MessagePublisher messagePublisher = new KafkaPublisher(configProvider);

        System.out.println("Initialized");
        new SensorMessagePipeline()
                .withSensorMessageSources(udpListeners)
                .withSensorMessageDeserializer(sensorMessageDeserializer)
                .withOutboundMessageSerializer(outboundMessageSerializer)
                .withMessagePublisher(messagePublisher)
                .runPipeline();
    }

}