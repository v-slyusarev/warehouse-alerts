package com.github.vslyusarev.warehousealerts.warehouse.adapter.outbound;

import com.github.vslyusarev.warehousealerts.warehouse.infrastructure.config.ConfigProvider;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.output.CorrelationMetadata;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.output.MessagePublisher;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.output.OutboundMessage;
import org.apache.kafka.clients.producer.ProducerRecord;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import java.util.HashMap;
import java.util.Map;

public class KafkaPublisher implements MessagePublisher {
    private final ConfigProvider configProvider;
    private final KafkaSender<byte[], byte[]> kafkaSender;

    public KafkaPublisher(ConfigProvider configProvider) {
        this.configProvider = configProvider;
        final Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", configProvider.getKafkaBooststrapServers());
        props.put("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        final SenderOptions<byte[], byte[]> senderOptions = SenderOptions.create(props);
        this.kafkaSender = KafkaSender.create(senderOptions);
    }

    @Override
    public Mono<Void> publish(Flux<OutboundMessage> messageFlux) {
        return kafkaSender.send(messageFlux.map(this::toSenderRecord))
                .then();
    }

    private SenderRecord<byte[], byte[], CorrelationMetadata> toSenderRecord(OutboundMessage message) {
        final var producerRecord = new ProducerRecord<>(configProvider.getTopicName(), message.key(), message.payload());
        return SenderRecord.create(producerRecord, message.correlationMetadata());
    }
}
