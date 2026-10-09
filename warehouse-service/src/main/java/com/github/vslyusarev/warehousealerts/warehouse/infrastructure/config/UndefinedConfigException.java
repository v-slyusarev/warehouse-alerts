package com.github.vslyusarev.warehousealerts.warehouse.infrastructure.config;

public class UndefinedConfigException extends RuntimeException {
    public UndefinedConfigException(String propertyKey) {
        super("Value for '%s' is not defined in application.properties".formatted(propertyKey).intern());
    }
}
