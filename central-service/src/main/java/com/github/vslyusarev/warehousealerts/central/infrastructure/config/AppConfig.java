package com.github.vslyusarev.warehousealerts.central.infrastructure.config;

import com.github.vslyusarev.warehousealerts.shared.config.KafkaConfig;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

public class AppConfig extends KafkaConfig implements ConfigProvider {
    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    private Map<SensorType, Integer> thresholds;
    private Duration maxEventAge;
    private boolean filterEnabled = false;
    private String consumerGroupId;

    public AppConfig(String propertiesFilePath) {
        super(propertiesFilePath);
        thresholds = Arrays.stream(SensorType.values())
                .collect(java.util.stream.Collectors.toMap(
                        sensorType -> sensorType,
                        sensorType -> Integer.parseInt(properties.getProperty("threshold.%s".formatted(sensorType.name())))
                )
        );

        String maxEventAgeString = properties.getProperty("EventFilter.maxEventAge");
        if (maxEventAgeString != null) {
            maxEventAge = Duration.parse(maxEventAgeString);
            filterEnabled = true;
        }

        consumerGroupId = required(properties, "kafka.consumer.groupId").intern();

        log.info("Config loaded");
    }

    @Override
    public Map<SensorType, Integer> getThresholds() {
        return Map.of();
    }

    @Override
    public Duration getMaxEventAge() {
        return null;
    }

    private void setThresholds(Map<SensorType, Integer> thresholds) {
        this.thresholds = thresholds;
    }

    public boolean isFilterEnabled() {
        return filterEnabled;
    }

    private void setFilterEnabled(boolean filterEnabled) {
        this.filterEnabled = filterEnabled;
    }

    @Override
    public String getConsumerGroupId() {
        return consumerGroupId;
    }
}
