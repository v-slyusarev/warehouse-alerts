package com.github.vslyusarev.warehousealerts.central.application.thresholds;

import com.github.vslyusarev.warehousealerts.central.application.model.Alert;
import com.github.vslyusarev.warehousealerts.central.application.state.SensorsState;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorType;

import java.util.Map;
import java.util.Optional;

public class DefaultThresholdAlertService implements ThresholdAlertService {
    private final SensorsState sensorsState;
    private final Map<SensorType, Integer> thresholds;

    public DefaultThresholdAlertService(SensorsState sensorsState, ThresholdProvider thresholdProvider) {
        this.thresholds = thresholdProvider.getThresholds();
        this.sensorsState = sensorsState;
    }

    @Override
    public Optional<Alert> process(SensorEvent sensorEvent) {
        Integer threshold = thresholds.get(sensorEvent.sensorType());
        Optional<Integer> lastValue = sensorsState.getAndUpdateState(sensorEvent.warehouseId(), sensorEvent.sensorId(), sensorEvent.value());
        if (threshold != null && sensorEvent.value() > threshold && (lastValue.isEmpty() || lastValue.get() <= threshold)) {
            return Optional.of(new Alert(
                    sensorEvent.warehouseId(),
                    sensorEvent.sensorId(),
                    sensorEvent.timestamp(),
                    threshold,
                    sensorEvent.value()
            ));
        }
        return Optional.empty();
    }
}
