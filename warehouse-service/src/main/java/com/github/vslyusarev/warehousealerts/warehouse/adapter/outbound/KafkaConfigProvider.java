package com.github.vslyusarev.warehousealerts.warehouse.adapter.outbound;

public interface KafkaConfigProvider {
    String getTopicName();

    String getBootstrapServers();
}
