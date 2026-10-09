package com.github.vslyusarev.warehousealerts.warehouse.infrastructure.config;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

public class AppConfig implements ConfigProvider {
    String warehouseId;
    String topicName;
    String bootstrapServers;
    long samplingIntervalMs;

    Map<SensorType, Integer> udpPorts;

    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);


    public static AppConfig load() throws IOException {
        final Properties properties = new Properties();

        try (InputStream inputStream = AppConfig.class.getResourceAsStream("/application.properties")) {
            if (inputStream == null) {
                throw new IllegalStateException("application.properties not found");
            }
            properties.load(inputStream);
        }

        final AppConfig appConfig = new AppConfig();

        appConfig.setWarehouseId(required(properties, "warehouse.id").intern());
        appConfig.setBootstrapServers(required(properties, "KafkaPublisher.bootstrapServers").intern());
        appConfig.setTopicName(required(properties, "KafkaPublisher.topic").intern());
        appConfig.setUdpPorts(loadUdpPorts(properties));
        appConfig.setSamplingIntervalMs(Long.parseLong(required(properties, "SensorMessageSampler.samplingIntervalMs")));
        log.info("Config loaded");
        return appConfig;
    }

    @Override
    public String getWarehouseId() {
        return warehouseId;
    }

    @Override
    public String getTopicName() {
        return topicName;
    }

    @Override
    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public long getSamplingIntervalMs() {
        return samplingIntervalMs;
    }

    @Override
    public int getUdpPort(SensorType sensorType) {
        final Integer port = udpPorts.get(sensorType);
        if (port == null) {
            throw new IllegalArgumentException("UDP port for sensor type " + sensorType + " is not configured");
        }
        return port;
    }

    private static Map<SensorType, Integer> loadUdpPorts(Properties properties) {
        return Arrays.stream(SensorType.values())
                .collect(java.util.stream.Collectors.toMap(
                        sensorType -> sensorType,
                        sensorType -> Integer.parseInt(required(properties, getPropertyName(sensorType)))
                ));
    }

    private static String getPropertyName(SensorType sensorType) {
        return "UdpListener.port.%s".formatted(sensorType.name()).intern();
    }

    private static String required(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new UndefinedConfigException(key);
        }
        return value.trim();
    }

    private AppConfig() {}

    private void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    private void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    private void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    private void setSamplingIntervalMs(long samplingIntervalMs) {
        this.samplingIntervalMs = samplingIntervalMs;
    }

    private void setUdpPorts(Map<SensorType, Integer> udpPorts) {
        this.udpPorts = udpPorts;
    }

}
