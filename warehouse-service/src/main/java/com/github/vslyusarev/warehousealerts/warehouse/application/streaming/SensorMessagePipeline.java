package com.github.vslyusarev.warehousealerts.warehouse.application.streaming;

import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.input.SensorMessage;
import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.input.SensorMessageDeserializer;
import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.input.SensorMessageSource;
import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.input.SensorMessageSourceFactory;
import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.output.MessagePublisher;
import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.output.OutboundMessage;
import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.output.OutboundMessageSerializer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class SensorMessagePipeline {
    private SensorMessageSourceFactory sensorMessageSourceFactory;
    private SensorMessageSampler sensorMessageSampler;
    private SensorMessageDeserializer sensorMessageDeserializer;
    private MessagePublisher messagePublisher;
    private OutboundMessageSerializer outboundMessageSerializer;

    public SensorMessagePipeline withSensorMessageSourceFactory(SensorMessageSourceFactory sensorMessageSourceFactory) {
        this.sensorMessageSourceFactory = sensorMessageSourceFactory;
        return this;
    }

    public SensorMessagePipeline withSensorMessageSampler(SensorMessageSampler sensorMessageSampler) {
        this.sensorMessageSampler = sensorMessageSampler;
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
        final List<Flux<SensorMessage>> messageSources = sensorMessageSourceFactory.createSources().stream()
                .map(SensorMessageSource::getMessages)
                .toList();

        Flux.merge(messageSources)
                .flatMap(this::processMessage)
                .transform(messagePublisher::publish)
                .blockLast();
    }

    private Mono<OutboundMessage> processMessage(SensorMessage sensorMessage) {
        return Mono.just(sensorMessage)
                .filter(sensorMessageSampler::accept)
                .map(message -> sensorMessageDeserializer.deserialize(message))
                .map(outboundMessageSerializer::serialize)
//                .doOnError(error -> log.error("Failed to process sensor message from {}", message.sender(), error))
                .onErrorResume(error -> Mono.empty());
    }
}
