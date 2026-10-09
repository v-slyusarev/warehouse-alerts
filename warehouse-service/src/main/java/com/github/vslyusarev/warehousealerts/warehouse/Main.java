package com.github.vslyusarev.warehousealerts.warehouse;

import com.github.vslyusarev.warehousealerts.warehouse.adapter.inbound.TimeProvider;
import com.github.vslyusarev.warehousealerts.warehouse.adapter.inbound.UdpListenerFactory;
import com.github.vslyusarev.warehousealerts.warehouse.adapter.outbound.KafkaPublisher;
import com.github.vslyusarev.warehousealerts.warehouse.application.sampling.InMemorySensorMessageSampler;
import com.github.vslyusarev.warehousealerts.warehouse.infrastructure.SystemTimeProvider;
import com.github.vslyusarev.warehousealerts.warehouse.infrastructure.config.AppConfig;
import com.github.vslyusarev.warehousealerts.warehouse.infrastructure.config.ConfigProvider;
import com.github.vslyusarev.warehousealerts.warehouse.infrastructure.serialization.ProtobufOutboundMessageSerializer;
import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.SensorMessageSampler;
import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.input.SensorMessageDeserializer;
import com.github.vslyusarev.warehousealerts.warehouse.infrastructure.serialization.DefaultSensorMessageDeserializer;
import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.SensorMessagePipeline;
import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.input.SensorMessageSourceFactory;
import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.output.MessagePublisher;
import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.output.OutboundMessageSerializer;

import java.io.IOException;


public class Main {
    public static void main(String[] args) {
        final ConfigProvider configProvider;
        try {
            configProvider = AppConfig.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final TimeProvider timeProvider = new SystemTimeProvider();
        final SensorMessageSampler sensorMessageSampler = new InMemorySensorMessageSampler(configProvider);
        final SensorMessageSourceFactory sensorMessageSourceFactory = new UdpListenerFactory(configProvider, timeProvider);
        final SensorMessageDeserializer sensorMessageDeserializer = new DefaultSensorMessageDeserializer(timeProvider);
        final OutboundMessageSerializer outboundMessageSerializer = new ProtobufOutboundMessageSerializer(configProvider);
        final MessagePublisher messagePublisher = new KafkaPublisher(configProvider);

        System.out.println("Initialized");
        new SensorMessagePipeline()
                .withSensorMessageSourceFactory(sensorMessageSourceFactory)
                .withSensorMessageSampler(sensorMessageSampler)
                .withSensorMessageDeserializer(sensorMessageDeserializer)
                .withOutboundMessageSerializer(outboundMessageSerializer)
                .withMessagePublisher(messagePublisher)
                .runPipeline();
    }
}