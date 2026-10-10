package com.github.vslyusarev.warehousealerts.central.adapter.inbound;

import com.github.vslyusarev.warehousealerts.shared.kafka.KafkaConfigProvider;

public interface KafkaConsumerConfigProvider extends KafkaConfigProvider {
    String getConsumerGroupId();
}
