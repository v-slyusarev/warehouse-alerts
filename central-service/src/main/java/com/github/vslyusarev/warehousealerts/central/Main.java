package com.github.vslyusarev.warehousealerts.central;

import com.github.vslyusarev.warehousealerts.central.adapter.inbound.KafkaConsumer;
import com.github.vslyusarev.warehousealerts.central.adapter.outbound.ConsoleAlertEmitter;
import com.github.vslyusarev.warehousealerts.central.application.filters.EventFilter;
import com.github.vslyusarev.warehousealerts.central.application.filters.OutdatedEventFilter;
import com.github.vslyusarev.warehousealerts.central.application.state.InMemorySensorsState;
import com.github.vslyusarev.warehousealerts.central.application.state.SensorsState;
import com.github.vslyusarev.warehousealerts.central.application.thresholds.DefaultThresholdAlertService;
import com.github.vslyusarev.warehousealerts.central.application.thresholds.ThresholdAlertService;
import com.github.vslyusarev.warehousealerts.central.infrastructure.config.AppConfig;
import com.github.vslyusarev.warehousealerts.central.infrastructure.config.ConfigProvider;
import com.github.vslyusarev.warehousealerts.central.streaming.*;
import com.github.vslyusarev.warehousealerts.shared.serialization.WarehouseMessageDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Application started");

        final ConfigProvider configProvider = new AppConfig("/application.properties");
        final EventSource eventSource = new KafkaConsumer(new WarehouseMessageDeserializer(), configProvider);
        final SensorsState sensorsState = new InMemorySensorsState();
        final ThresholdAlertService thresholdAlertService = new DefaultThresholdAlertService(sensorsState, configProvider);
        final AlertEmitter alertEmitter = new ConsoleAlertEmitter();
        final DefaultEventProcessor eventProcessor = new DefaultEventProcessor(thresholdAlertService, alertEmitter);
        if(configProvider.isFilterEnabled()) {
            final EventFilter eventFilter = new OutdatedEventFilter(configProvider);
            eventProcessor.setEventFilter(eventFilter);
        }
        final EventPipeline eventPipeline = new EventPipeline(eventSource, eventProcessor);
        eventPipeline.run();

    }
}
