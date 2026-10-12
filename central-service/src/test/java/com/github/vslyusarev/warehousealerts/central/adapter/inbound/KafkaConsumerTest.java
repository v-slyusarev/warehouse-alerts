package com.github.vslyusarev.warehousealerts.central.adapter.inbound;

import com.github.vslyusarev.warehousealerts.shared.contracts.SensorEvent;
import com.github.vslyusarev.warehousealerts.shared.contracts.SensorType;
import com.github.vslyusarev.warehousealerts.shared.serialization.DeserializationException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.sender.TransactionManager;

import java.time.Instant;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class KafkaConsumerTest {
    private static class MockReceiverOffset implements ReceiverOffset {
        private boolean acknowledged = false;

        @Override
        public TopicPartition topicPartition() {
            return null;
        }

        @Override
        public long offset() {
            return 0;
        }

        @Override
        public void acknowledge() {
            acknowledged = true;
        }

        @Override
        public Mono<Void> commit() {
            return null;
        }

        public boolean isAcknowledged() {
            return acknowledged;
        }
    }

    private static class MockKafkaReceiver implements KafkaReceiver<byte[], byte[]> {
        private final List<MockReceiverOffset> mockReceiverOffsets;

        private MockKafkaReceiver(List<MockReceiverOffset> mockReceiverOffsets) {
            this.mockReceiverOffsets = mockReceiverOffsets;
        }

        @Override
        public Flux<ReceiverRecord<byte[], byte[]>> receive(Integer prefetch) {
            return Flux.fromStream(mockReceiverOffsets.stream()
                    .map(offset -> new ReceiverRecord<>(
                            new ConsumerRecord<>("warehouse-messages",
                                    0,
                                    0,
                                    new byte[0],
                                    new byte[0]), offset))
            );
        }

        @Override
        public Flux<Flux<ReceiverRecord<byte[], byte[]>>> receiveBatch(Integer prefetch) {
            return null;
        }

        @Override
        public Flux<Flux<ConsumerRecord<byte[], byte[]>>> receiveAutoAck(Integer prefetch) {
            return null;
        }

        @Override
        public Flux<ConsumerRecord<byte[], byte[]>> receiveAtmostOnce(Integer prefetch) {
            return null;
        }

        @Override
        public Flux<Flux<ConsumerRecord<byte[], byte[]>>> receiveExactlyOnce(TransactionManager transactionManager, Integer prefetch) {
            return null;
        }

        @Override
        public <T> Mono<T> doOnConsumer(Function<Consumer<byte[], byte[]>, ? extends T> function) {
            return null;
        }
    }

    @Test
    void testListeningHappyPath() {
        final MockReceiverOffset mockReceiverOffset = new MockReceiverOffset();
        final KafkaConsumer kafkaConsumer = new KafkaConsumer(
                new MockKafkaReceiver(List.of(mockReceiverOffset)),
                bytes -> new SensorEvent("w1", "t1", SensorType.TEMPERATURE, 0, Instant.EPOCH),
                ((sensorEvent, onSuccess) -> {
                    onSuccess.run();
                    return Mono.empty();
                })
        );

        kafkaConsumer.run();

        assertTrue(mockReceiverOffset.isAcknowledged());
    }

    @Test
    void testListeningResumesIfDeserializerFails() {
        final List<MockReceiverOffset> mockReceiverOffsets = List.of(new MockReceiverOffset(), new MockReceiverOffset());
        final KafkaConsumer kafkaConsumer = new KafkaConsumer(
                new MockKafkaReceiver(mockReceiverOffsets),
                bytes -> {
                    throw new DeserializationException(null);
                },
                ((sensorEvent, onSuccess) -> {
                    onSuccess.run();
                    return Mono.empty();
                })
        );

        kafkaConsumer.run();

        assertTrue(mockReceiverOffsets.stream().allMatch(MockReceiverOffset::isAcknowledged));
    }

    @Test
    void testListeningHaltsIfProcessorFails() {
        final List<MockReceiverOffset> mockReceiverOffsets = List.of(new MockReceiverOffset(), new MockReceiverOffset());
        final KafkaConsumer kafkaConsumer = new KafkaConsumer(
                new MockKafkaReceiver(mockReceiverOffsets),
                bytes -> new SensorEvent("w1", "t1", SensorType.TEMPERATURE, 0, Instant.EPOCH),
                ((sensorEvent, onSuccess) -> Mono.fromCallable(
                        () -> {
                            throw new RuntimeException();
                        }
                ))
        );

        assertThrows(RuntimeException.class, kafkaConsumer::run);

        assertTrue(mockReceiverOffsets.stream().noneMatch(MockReceiverOffset::isAcknowledged));
    }

}