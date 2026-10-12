package com.github.vslyusarev.warehousealerts.central;

import com.github.vslyusarev.warehousealerts.central.adapter.inbound.KafkaConsumer;
import com.github.vslyusarev.warehousealerts.central.adapter.outbound.ConsoleAlertEmitter;
import com.github.vslyusarev.warehousealerts.central.application.filters.EventFilter;
import com.github.vslyusarev.warehousealerts.central.application.filters.OutdatedEventFilter;
import com.github.vslyusarev.warehousealerts.central.application.state.InMemorySensorsState;
import com.github.vslyusarev.warehousealerts.central.application.state.SensorsState;
import com.github.vslyusarev.warehousealerts.central.application.thresholds.DefaultThresholdAlertService;
import com.github.vslyusarev.warehousealerts.central.application.thresholds.ThresholdAlertService;
import com.github.vslyusarev.warehousealerts.central.infrastructure.SystemTimeProvider;
import com.github.vslyusarev.warehousealerts.central.infrastructure.config.AppConfig;
import com.github.vslyusarev.warehousealerts.central.infrastructure.config.ConfigProvider;
import com.github.vslyusarev.warehousealerts.central.streaming.*;
import com.github.vslyusarev.warehousealerts.shared.serialization.DefaultWarehouseMessageDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.util.retry.Retry;

import java.time.Duration;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Application started");

        final ConfigProvider configProvider = new AppConfig("/application.properties");
        final SensorsState sensorsState = new InMemorySensorsState();
        final ThresholdAlertService thresholdAlertService = new DefaultThresholdAlertService(sensorsState, configProvider);
        final AlertEmitter alertEmitter = new ConsoleAlertEmitter();
        DefaultEventProcessor eventProcessor = new DefaultEventProcessor(thresholdAlertService,
                alertEmitter,
                Retry.backoff(5, Duration.ofMillis(200)));
        if(configProvider.isFilterEnabled()) {
            final EventFilter eventFilter = new OutdatedEventFilter(new SystemTimeProvider(), configProvider);
            eventProcessor = new FilteringEventProcessor(eventProcessor, eventFilter);
        }
        final KafkaConsumer kafkaConsumer = new KafkaConsumer(configProvider, new DefaultWarehouseMessageDeserializer(), eventProcessor);
        kafkaConsumer.run();
    }
}
