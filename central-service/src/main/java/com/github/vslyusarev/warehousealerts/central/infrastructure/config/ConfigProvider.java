package com.github.vslyusarev.warehousealerts.central.infrastructure.config;

import com.github.vslyusarev.warehousealerts.central.adapter.inbound.KafkaConsumerConfigProvider;
import com.github.vslyusarev.warehousealerts.central.application.filters.EventFilterConfigProvider;
import com.github.vslyusarev.warehousealerts.central.application.thresholds.ThresholdProvider;

public interface ConfigProvider extends ThresholdProvider, EventFilterConfigProvider, KafkaConsumerConfigProvider {
}
