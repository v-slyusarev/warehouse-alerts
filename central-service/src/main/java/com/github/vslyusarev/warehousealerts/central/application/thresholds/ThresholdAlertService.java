package com.github.vslyusarev.warehousealerts.central.application.thresholds;

import com.github.vslyusarev.warehousealerts.central.application.model.Alert;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;

import java.util.Optional;

public interface ThresholdAlertService {
    Optional<Alert> process(SensorEvent sensorEvent);
}
