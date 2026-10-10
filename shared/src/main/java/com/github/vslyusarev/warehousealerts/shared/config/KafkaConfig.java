package com.github.vslyusarev.warehousealerts.shared.config;

import com.github.vslyusarev.warehousealerts.shared.kafka.KafkaConfigProvider;

public class KafkaConfig extends BaseConfig implements KafkaConfigProvider {
    String topicName;
    String bootstrapServers;

    protected KafkaConfig(String propertiesFilePath) {
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
