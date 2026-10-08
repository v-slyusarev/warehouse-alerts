package com.github.vslyusarev.warehousealerts.warehouse.streaming;

import com.github.vslyusarev.warehousealerts.warehouse.application.model.SensorType;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.input.SensorMessageDeserializer;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.input.SensorMessageSource;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.output.MessagePublisher;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.output.OutboundMessageSerializer;
import reactor.core.publisher.Flux;

import java.util.Map;

public class SensorMessagePipeline {
    private Map<SensorType, ? extends SensorMessageSource> sensorMessageSources;
    private SensorMessageDeserializer sensorMessageDeserializer;
    private MessagePublisher messagePublisher;
    private OutboundMessageSerializer outboundMessageSerializer;

    public SensorMessagePipeline(Map<SensorType, SensorMessageSource> sensorMessageSources, SensorMessageDeserializer sensorMessageDeserializer) {
        this.sensorMessageSources = sensorMessageSources;
        this.sensorMessageDeserializer = sensorMessageDeserializer;
    }

    public SensorMessagePipeline() {
    }

    public SensorMessagePipeline withSensorMessageSources(Map<SensorType, ? extends SensorMessageSource> sensorMessageSources) {
        this.sensorMessageSources = sensorMessageSources;
        return this;
    }

    public SensorMessagePipeline withSensorMessageDeserializer(SensorMessageDeserializer sensorMessageDeserializer) {
        this.sensorMessageDeserializer = sensorMessageDeserializer;
        return this;
    }

    public SensorMessagePipeline withMessagePublisher(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
        return this;
    }

    public SensorMessagePipeline withOutboundMessageSerializer(OutboundMessageSerializer outboundMessageSerializer) {
        this.outboundMessageSerializer = outboundMessageSerializer;
        return this;
    }

    public void runPipeline() {
        assert sensorMessageSources != null : "Sensor message sources must be provided";
        assert sensorMessageDeserializer != null : "Sensor message deserializer must be provided";
        assert outboundMessageSerializer != null : "Outbound message serializer must be provided";
        assert messagePublisher != null : "Message publisher must be provided";

        final var sensorMeasurementSources = sensorMessageSources.entrySet().stream()
                .map(entry -> entry.getValue().getMessages()
                .map(message -> sensorMessageDeserializer.deserialize(entry.getKey(), message)))
                .toList();

        Flux.merge(sensorMeasurementSources)
                .map(outboundMessageSerializer::serialize)
                .doOnNext(System.out::println)
                .transform(messagePublisher::publish)
                .doOnError(Throwable::printStackTrace)
                .blockLast();
    }
}
