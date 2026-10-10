package com.github.vslyusarev.warehousealerts.warehouse.adapter.outbound;

import com.github.vslyusarev.warehousealerts.shared.kafka.KafkaConfigProvider;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.output.CorrelationMetadata;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.output.MessagePublisher;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.output.OutboundMessage;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

import java.util.Map;

public class KafkaPublisher implements MessagePublisher {
    private final KafkaSender<byte[], byte[]> kafkaSender;
    private final String topicName;

    private static final Logger log = LoggerFactory.getLogger(KafkaPublisher.class);

    public KafkaPublisher(KafkaConfigProvider configProvider) {
        final Map<String, Object> props = Map.of(
         "bootstrap.servers", configProvider.getBootstrapServers(),
         "key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer",
         "value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer"
        );
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
                        log.warn("Failed to publish Kafka message for sensor {} received at {}",
                                result.correlationMetadata().sensorId(),
                                result.correlationMetadata().timestamp(),
                                result.exception());
                    }
                })
                .then();
    }

    private SenderRecord<byte[], byte[], CorrelationMetadata> toSenderRecord(OutboundMessage message) {
        final ProducerRecord<byte[], byte[]> producerRecord = new ProducerRecord<>(topicName, message.key(), message.payload());
        return SenderRecord.create(producerRecord, message.correlationMetadata());
    }
}
