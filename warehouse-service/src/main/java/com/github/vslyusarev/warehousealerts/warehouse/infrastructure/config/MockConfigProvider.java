package com.github.vslyusarev.warehousealerts.warehouse.infrastructure.config;

import java.util.UUID;

public class MockConfigProvider implements ConfigProvider {
    @Override
    public String getWarehouseId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String getTopicName() {
        return "warehouse-messages";
    }

    @Override
    public String getKafkaBooststrapServers() {
        return "127.0.0.1:9092";
    }
}
