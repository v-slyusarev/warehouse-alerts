package com.github.vslyusarev.warehousealerts.central.adapter.inbound;

import com.github.vslyusarev.warehousealerts.central.streaming.EventProcessor;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;
import com.github.vslyusarev.warehousealerts.shared.serialization.WarehouseMessageDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.List;
import java.util.Map;

public class KafkaConsumer {
    private final KafkaReceiver<byte[], byte[]> kafkaReceiver;
    private final WarehouseMessageDeserializer warehouseMessageDeserializer;
    private final EventProcessor eventProcessor;

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    public KafkaConsumer(WarehouseMessageDeserializer warehouseMessageDeserializer, KafkaConsumerConfigProvider configProvider, EventProcessor eventProcessor) {
        this.warehouseMessageDeserializer = warehouseMessageDeserializer;
        this.eventProcessor = eventProcessor;
        Map<String, Object> props = Map.of(
                "bootstrap.servers", configProvider.getBootstrapServers(),
                "group.id", configProvider.getConsumerGroupId(),
                "key.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer",
                "value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer",
                "auto.offset.reset", "latest",
                "enable.auto.commit", "false"
        );

        ReceiverOptions<byte[], byte[]> options = ReceiverOptions.<byte[], byte[]>create(props)
                .subscription(List.of(configProvider.getTopicName()));

        this.kafkaReceiver = KafkaReceiver.create(options);
        log.info("Kafka consumer for topic {}, bootstrap servers {} and consumer group {} initialized",
                configProvider.getTopicName(),
                configProvider.getBootstrapServers(),
                configProvider.getConsumerGroupId());
    }

    public void run() {
        kafkaReceiver.receive()
                .concatMap(record -> {
                    SensorEvent event = warehouseMessageDeserializer.toSensorEvent(record.value());
                    return eventProcessor.consumeEvent(event, record.receiverOffset()::acknowledge);
                })
                .blockLast();
    }
}
