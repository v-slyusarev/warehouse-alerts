package com.github.vslyusarev.warehousealerts.central.adapter.inbound;

import com.github.vslyusarev.warehousealerts.central.streaming.EventProcessor;
import com.github.vslyusarev.warehousealerts.central.streaming.EventSource;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;
import com.github.vslyusarev.warehousealerts.shared.serialization.WarehouseMessageDeserializer;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.List;
import java.util.Map;

public class KafkaConsumer implements EventSource {
    private final KafkaReceiver<byte[], byte[]> kafkaReceiver;
    private final WarehouseMessageDeserializer warehouseMessageDeserializer;

    public KafkaConsumer(WarehouseMessageDeserializer warehouseMessageDeserializer, KafkaConsumerConfigProvider configProvider) {
        this.warehouseMessageDeserializer = warehouseMessageDeserializer;
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
    }

    @Override
    public Disposable subscribe(EventProcessor processor) {
        return kafkaReceiver.receive()
                .concatMap(record -> {
                    SensorEvent event = warehouseMessageDeserializer.toSensorEvent(record.value());
                    return processor.consumeEvent(event, record.receiverOffset()::acknowledge);
                })
                .subscribe();
    }
}
