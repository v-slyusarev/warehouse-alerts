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

    private final Map<SensorType, Integer> thresholds;
    private final Duration maxEventAge;
    private final boolean filterEnabled;
    private final String consumerGroupId;

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
        } else {
            maxEventAge = null;
            filterEnabled = false;
        }

        consumerGroupId = required(properties, "kafka.consumer.groupId").intern();

        log.info("Config loaded");
    }

    @Override
    public Map<SensorType, Integer> getThresholds() {
        return thresholds;
    }

    @Override
    public Duration getMaxEventAge() {
        return maxEventAge;
    }

    public boolean isFilterEnabled() {
        return filterEnabled;
    }

    @Override
    public String getConsumerGroupId() {
        return consumerGroupId;
    }
}
