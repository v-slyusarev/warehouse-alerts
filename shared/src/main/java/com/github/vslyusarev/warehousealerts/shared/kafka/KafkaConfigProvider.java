package com.github.vslyusarev.warehousealerts.shared.kafka;

public interface KafkaConfigProvider {
    String getTopicName();

    String getBootstrapServers();
}
