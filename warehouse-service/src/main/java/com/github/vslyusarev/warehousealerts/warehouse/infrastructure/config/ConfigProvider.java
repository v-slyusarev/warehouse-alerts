package com.github.vslyusarev.warehousealerts.warehouse.infrastructure.config;

public interface ConfigProvider {
    String getWarehouseId();

    String getTopicName();

    String getKafkaBooststrapServers();
}
