package com.github.vslyusarev.warehousealerts.warehouse;

import com.github.vslyusarev.warehousealerts.shared.serialization.WarehouseMessageSerializer;
import com.github.vslyusarev.warehousealerts.warehouse.adapter.inbound.TimeProvider;
import com.github.vslyusarev.warehousealerts.warehouse.adapter.inbound.UdpListenerFactory;
import com.github.vslyusarev.warehousealerts.warehouse.adapter.outbound.KafkaPublisher;
import com.github.vslyusarev.warehousealerts.warehouse.application.processing.DefaultSensorMessageProcessor;
import com.github.vslyusarev.warehousealerts.warehouse.application.processing.SensorMessageProcessor;
import com.github.vslyusarev.warehousealerts.warehouse.infrastructure.SystemTimeProvider;
import com.github.vslyusarev.warehousealerts.warehouse.infrastructure.config.AppConfig;
import com.github.vslyusarev.warehousealerts.warehouse.infrastructure.config.ConfigProvider;
import com.github.vslyusarev.warehousealerts.warehouse.infrastructure.serialization.DefaultOutboundMessageWriter;
import com.github.vslyusarev.warehousealerts.warehouse.application.processing.SensorMessageDeserializer;
import com.github.vslyusarev.warehousealerts.warehouse.infrastructure.serialization.DefaultSensorMessageDeserializer;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.SensorMessagePipeline;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.input.SensorMessageSourceFactory;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.output.MessagePublisher;
import com.github.vslyusarev.warehousealerts.warehouse.application.processing.OutboundMessageWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Application started");

        final ConfigProvider configProvider = new AppConfig("/application.properties");
        final TimeProvider timeProvider = new SystemTimeProvider();
        final SensorMessageSourceFactory sensorMessageSourceFactory = new UdpListenerFactory(configProvider, timeProvider);
        final SensorMessageDeserializer sensorMessageDeserializer = new DefaultSensorMessageDeserializer();
        final OutboundMessageWriter outboundMessageSerializer = new DefaultOutboundMessageWriter(new WarehouseMessageSerializer());
        final SensorMessageProcessor sensorMessageProcessor = new DefaultSensorMessageProcessor(sensorMessageDeserializer, outboundMessageSerializer, configProvider);
        final MessagePublisher messagePublisher = new KafkaPublisher(configProvider);

        log.info("Initialization completed");

        new SensorMessagePipeline(sensorMessageSourceFactory, sensorMessageProcessor, messagePublisher)
                .runPipeline();
    }
}