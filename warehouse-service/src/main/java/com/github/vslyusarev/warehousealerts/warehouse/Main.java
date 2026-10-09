package com.github.vslyusarev.warehousealerts.warehouse;

import com.github.vslyusarev.warehousealerts.warehouse.adapter.inbound.TimeProvider;
import com.github.vslyusarev.warehousealerts.warehouse.adapter.inbound.UdpListenerFactory;
import com.github.vslyusarev.warehousealerts.warehouse.adapter.outbound.KafkaPublisher;
import com.github.vslyusarev.warehousealerts.warehouse.application.processing.DefaultSensorMessageProcessor;
import com.github.vslyusarev.warehousealerts.warehouse.application.processing.SensorMessageProcessor;
import com.github.vslyusarev.warehousealerts.warehouse.application.sampling.InMemorySensorMessageSampler;
import com.github.vslyusarev.warehousealerts.warehouse.infrastructure.SystemTimeProvider;
import com.github.vslyusarev.warehousealerts.warehouse.infrastructure.config.AppConfig;
import com.github.vslyusarev.warehousealerts.warehouse.infrastructure.config.ConfigProvider;
import com.github.vslyusarev.warehousealerts.warehouse.infrastructure.serialization.ProtobufOutboundMessageSerializer;
import com.github.vslyusarev.warehousealerts.warehouse.application.sampling.SensorMessageSampler;
import com.github.vslyusarev.warehousealerts.warehouse.application.processing.SensorMessageDeserializer;
import com.github.vslyusarev.warehousealerts.warehouse.infrastructure.serialization.DefaultSensorMessageDeserializer;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.SensorMessagePipeline;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.input.SensorMessageSourceFactory;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.output.MessagePublisher;
import com.github.vslyusarev.warehousealerts.warehouse.application.processing.OutboundMessageSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;


public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Application started");

        final ConfigProvider configProvider;
        try {
            configProvider = AppConfig.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final TimeProvider timeProvider = new SystemTimeProvider();
        final SensorMessageSampler sensorMessageSampler = new InMemorySensorMessageSampler(configProvider);
        final SensorMessageSourceFactory sensorMessageSourceFactory = new UdpListenerFactory(configProvider, timeProvider);
        final SensorMessageDeserializer sensorMessageDeserializer = new DefaultSensorMessageDeserializer();
        final OutboundMessageSerializer outboundMessageSerializer = new ProtobufOutboundMessageSerializer();
        final SensorMessageProcessor sensorMessageProcessor = new DefaultSensorMessageProcessor(sensorMessageDeserializer, outboundMessageSerializer, configProvider);
        final MessagePublisher messagePublisher = new KafkaPublisher(configProvider);

        log.info("Initialization completed");

        new SensorMessagePipeline()
                .withSensorMessageSourceFactory(sensorMessageSourceFactory)
                .withSensorMessageSampler(sensorMessageSampler)
                .withSensorMessageProcessor(sensorMessageProcessor)
                .withMessagePublisher(messagePublisher)
                .runPipeline();
    }
}