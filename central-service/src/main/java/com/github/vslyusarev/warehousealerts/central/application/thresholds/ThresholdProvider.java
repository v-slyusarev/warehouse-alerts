package com.github.vslyusarev.warehousealerts.central.application.thresholds;

import com.github.vslyusarev.warehousealerts.shared.contracts.SensorType;

import java.util.Map;

public interface ThresholdProvider {
    Map<SensorType, Integer> getThresholds();
}
