package com.github.vslyusarev.warehousealerts.warehouse.infrastructure.config;

import com.github.vslyusarev.warehousealerts.shared.config.KafkaConfig;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

public class AppConfig extends KafkaConfig implements ConfigProvider {
    private final String warehouseId;
    private final Map<SensorType, Integer> udpPorts;

    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    public AppConfig(String propertiesFilePath) {
        super(propertiesFilePath);

        warehouseId = (required(properties, "warehouse.id").intern());
        udpPorts = (loadUdpPorts(properties));

        log.info("Config loaded");
    }

    @Override
    public String getWarehouseId() {
        return warehouseId;
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
}
