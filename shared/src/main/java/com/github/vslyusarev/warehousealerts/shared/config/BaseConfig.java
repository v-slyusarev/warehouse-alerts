package com.github.vslyusarev.warehousealerts.shared.config;

import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.Properties;

public abstract class BaseConfig {
    protected final Properties properties;

    protected BaseConfig(String propertiesFilePath) {
        final Properties properties = new Properties();

        try (InputStream inputStream = BaseConfig.class.getResourceAsStream(propertiesFilePath)) {
            if (inputStream == null) {
                throw new MissingResourceException("%s not found".formatted(propertiesFilePath), this.getClass().getName(), propertiesFilePath);
            }
            properties.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config", e);
        }

        this.properties = properties;
    }

    protected static String required(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new UndefinedConfigException(key);
        }
        return value.trim();
    }
}
