package com.github.vslyusarev.warehousealerts.warehouse.streaming;

import com.github.vslyusarev.warehousealerts.warehouse.application.processing.OutboundMessageWriter;
import com.github.vslyusarev.warehousealerts.warehouse.application.processing.SensorMessageProcessor;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.input.SensorMessage;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.input.SensorMessageSource;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.input.SensorMessageSourceFactory;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.output.MessagePublisher;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.output.OutboundMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.List;

public class SensorMessagePipeline {
    private final SensorMessageSourceFactory sensorMessageSourceFactory;
    private final SensorMessageProcessor sensorMessageProcessor;
    private final MessagePublisher messagePublisher;
    private final Retry retryPolicy;

    private static final Logger log = LoggerFactory.getLogger(SensorMessagePipeline.class);

    public SensorMessagePipeline(SensorMessageSourceFactory sensorMessageSourceFactory, SensorMessageProcessor sensorMessageProcessor, MessagePublisher messagePublisher, Retry retryPolicy) {
        this.sensorMessageSourceFactory = sensorMessageSourceFactory;
        this.sensorMessageProcessor = sensorMessageProcessor;
        this.messagePublisher = messagePublisher;
        this.retryPolicy = retryPolicy;
    }

    public void runPipeline() {
        final List<Flux<SensorMessage>> messageSources = sensorMessageSourceFactory.createSources().stream()
                .map(SensorMessageSource::getMessages)
                .toList();

        Flux.merge(messageSources)
                .flatMap(this::processMessage)
                .transform(this::publishWithErrorHandling)
                .blockLast();
    }

    private Mono<OutboundMessage> processMessage(SensorMessage sensorMessage) {
        return Mono.just(sensorMessage)
                .map(sensorMessageProcessor::process)
                .doOnError(error -> log.warn("Failed to process sensor message from {}", sensorMessage.sender(), error))
                .onErrorResume(error -> Mono.empty());
    }

    private Mono<Void> publishWithErrorHandling(Flux<OutboundMessage> outboundMessagesFlux) {
        return Mono.defer(() -> messagePublisher.publish(outboundMessagesFlux))
                .doOnError(error ->
                        log.warn("Failed to publish messages; applying retry policy", error))
                .retryWhen(retryPolicy);
    }
}
