package com.github.vslyusarev.warehousealerts.shared.config;

import com.github.vslyusarev.warehousealerts.shared.kafka.KafkaConfigProvider;

public class KafkaConfig extends BaseConfig implements KafkaConfigProvider {
    private final String topicName;
    private final String bootstrapServers;

    public KafkaConfig(String propertiesFilePath) {
        super(propertiesFilePath);
        this.bootstrapServers = required(properties, "kafka.bootstrapServers").intern();
        this.topicName = required(properties, "kafka.topic").intern();
    }

    @Override
    public String getTopicName() {
        return topicName;
    }

    @Override
    public String getBootstrapServers() {
        return bootstrapServers;
    }
}
