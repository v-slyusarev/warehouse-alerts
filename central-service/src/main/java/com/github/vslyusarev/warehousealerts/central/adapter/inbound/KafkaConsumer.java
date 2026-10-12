package com.github.vslyusarev.warehousealerts.central.adapter.inbound;

import com.github.vslyusarev.warehousealerts.central.streaming.EventProcessor;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;
import com.github.vslyusarev.warehousealerts.shared.serialization.DeserializationException;
import com.github.vslyusarev.warehousealerts.shared.serialization.WarehouseMessageDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.List;
import java.util.Map;

public class KafkaConsumer {
    private final KafkaReceiver<byte[], byte[]> kafkaReceiver;
    private final WarehouseMessageDeserializer warehouseMessageDeserializer;
    private final EventProcessor eventProcessor;

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    KafkaConsumer(KafkaReceiver<byte[], byte[]> kafkaReceiver, WarehouseMessageDeserializer warehouseMessageDeserializer, EventProcessor eventProcessor) {
        this.kafkaReceiver = kafkaReceiver;
        this.warehouseMessageDeserializer = warehouseMessageDeserializer;
        this.eventProcessor = eventProcessor;
    }

    public KafkaConsumer(KafkaConsumerConfigProvider configProvider, WarehouseMessageDeserializer warehouseMessageDeserializer, EventProcessor eventProcessor) {
        this(KafkaReceiver.create(ReceiverOptions.<byte[], byte[]>create(Map.of(
                        "bootstrap.servers", configProvider.getBootstrapServers(),
                        "group.id", configProvider.getConsumerGroupId(),
                        "key.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer",
                        "value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer",
                        "auto.offset.reset", "latest",
                        "enable.auto.commit", "false"
                ))
                .subscription(List.of(configProvider.getTopicName()))),
                warehouseMessageDeserializer,
                eventProcessor);
        log.info("Kafka consumer for topic {}, bootstrap servers {} and consumer group {} initialized",
                configProvider.getTopicName(),
                configProvider.getBootstrapServers(),
                configProvider.getConsumerGroupId());
    }

    public void run() {
        kafkaReceiver.receive()
                .concatMap(record -> {
                    try {
                        SensorEvent event = warehouseMessageDeserializer.toSensorEvent(record.value());
                        return eventProcessor.consumeEvent(event, record.receiverOffset()::acknowledge)
                                .doOnError(error -> log.error("Failed to process event: {}", event, error));
                    } catch (DeserializationException e) {
                        log.warn("Failed to deserialize message at offset {}. Skipping this message", record.receiverOffset(), e);
                        record.receiverOffset().acknowledge();
                        return Mono.empty();
                    }
                })
                .blockLast();
    }
}
