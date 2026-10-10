package com.github.vslyusarev.warehousealerts.shared.config;

public class UndefinedConfigException extends RuntimeException {
    public UndefinedConfigException(String propertyKey) {
        super("Value for '%s' is not defined in the config file".formatted(propertyKey).intern());
    }
}
