package com.github.vslyusarev.warehousealerts.warehouse.adapter.outbound;

import com.github.vslyusarev.warehousealerts.warehouse.infrastructure.config.ConfigProvider;
import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.output.CorrelationMetadata;
import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.output.MessagePublisher;
import com.github.vslyusarev.warehousealerts.warehouse.application.streaming.output.OutboundMessage;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

import java.util.HashMap;
import java.util.Map;

public class KafkaPublisher implements MessagePublisher {
    private final KafkaSender<byte[], byte[]> kafkaSender;
    private final String topicName;

    private static final Logger log = LoggerFactory.getLogger(KafkaPublisher.class);

    public KafkaPublisher(ConfigProvider configProvider) {
        final Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", configProvider.getBootstrapServers());
        props.put("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        final SenderOptions<byte[], byte[]> senderOptions = SenderOptions.create(props);
        this.kafkaSender = KafkaSender.create(senderOptions);
        this.topicName = configProvider.getTopicName();
        log.info("Kafka publisher for topic {} and bootstrap servers {} initialized", topicName, configProvider.getBootstrapServers());
    }

    @Override
    public Mono<Void> publish(Flux<OutboundMessage> messageFlux) {
        return kafkaSender.send(messageFlux.map(this::toSenderRecord))
                .doOnNext(result -> {
                    if (result.exception() != null) {
                        log.warn("Failed to publish Kafka message", result.exception());
                    }
                })
                .then();
    }

    private SenderRecord<byte[], byte[], CorrelationMetadata> toSenderRecord(OutboundMessage message) {
        final var producerRecord = new ProducerRecord<>(topicName, message.key(), message.payload());
        return SenderRecord.create(producerRecord, message.correlationMetadata());
    }
}
