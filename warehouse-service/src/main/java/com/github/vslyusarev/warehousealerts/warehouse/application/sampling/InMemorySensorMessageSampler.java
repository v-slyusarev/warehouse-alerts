package com.github.vslyusarev.warehousealerts.warehouse.application.sampling;

import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.SensorMessageSampler;
import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.input.SensorMessage;

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
                    long current = message.systemTimestampNanos();

                    if (lastMessageTimestamp == null || current > lastMessageTimestamp + samplingIntervalNanos) {
                        accepted[0] = true;
                        return current;
                    }

                    return lastMessageTimestamp;
                }
        );

        return accepted[0];
    }}
