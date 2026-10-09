package com.github.vslyusarev.warehousealerts.warehouse.application.sampling;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.input.SensorMessage;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySensorMessageSampler implements SensorMessageSampler {
    private final Map<SocketAddress, Long> lastMessageTimestampBySender = new ConcurrentHashMap<>();
    private final long samplingIntervalNanos;

    public InMemorySensorMessageSampler(SamplingConfigProvider samplingConfigProvider) {
        this.samplingIntervalNanos = samplingConfigProvider.getSamplingIntervalMs() * 1_000_000;
    }

    private final ThreadLocal<boolean[]> acceptedHolder =
            ThreadLocal.withInitial(() -> new boolean[1]);

    @Override
    public boolean accept(SensorMessage message) {
        boolean[] accepted = acceptedHolder.get();
        accepted[0] = false;

        lastMessageTimestampBySender.compute(
                message.sender(),
                (sender, lastMessageTimestamp) -> {
                    final long currentMessageTimestamp = message.monotonicTimestamp();

                    if (lastMessageTimestamp == null || currentMessageTimestamp > lastMessageTimestamp + samplingIntervalNanos) {
                        accepted[0] = true;
                        return currentMessageTimestamp;
                    }

                    return lastMessageTimestamp;
                }
        );

        return accepted[0];
    }}
