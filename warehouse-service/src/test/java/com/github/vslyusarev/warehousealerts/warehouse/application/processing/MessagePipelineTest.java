package com.github.vslyusarev.warehousealerts.warehouse.application.processing;

import com.github.vslyusarev.warehousealerts.shared.contracts.SensorType;
import com.github.vslyusarev.warehousealerts.warehouse.application.GeneralConfigProvider;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.input.SensorMeasurement;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.input.SensorMessage;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.output.CorrelationMetadata;
import com.github.vslyusarev.warehousealerts.warehouse.application.model.output.OutboundMessage;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.SensorMessagePipeline;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.input.SensorMessageSource;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.input.SensorMessageSourceFactory;
import com.github.vslyusarev.warehousealerts.warehouse.streaming.output.MessagePublisher;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class MessagePipelineTest {
    private static final SensorMeasurement sensorMeasurement = new SensorMeasurement("t1", 30);
    private static final CorrelationMetadata correlationMetadata = new CorrelationMetadata("t1", Instant.EPOCH);
    private static final SensorMessageDeserializer mockDeserializer = bytes -> sensorMeasurement;
    private static final SensorMessageDeserializer failingDeserializer = bytes -> { throw new RuntimeException(); };
    private static final OutboundMessageWriter mockOutboundMessageWriter = sensorEvent -> new OutboundMessage(null, null, correlationMetadata);
    private static final OutboundMessageWriter failingOutboundMessageWriter = sensorEvent -> { throw new RuntimeException(); };
    private static final GeneralConfigProvider mockGeneralConfigProvider = () -> "w1";
    private static final int MAX_RETRIES = 5;
    private static final Retry retryPolicy = Retry.max(MAX_RETRIES);
    private static final int MESSAGES_PER_SENSOR_TYPE = 10;
    private static final int expectedMessageCount = SensorType.values().length * MESSAGES_PER_SENSOR_TYPE;


    private static class MessageCounter {
        private final AtomicInteger messageCount = new AtomicInteger(0);

        protected void increment() {
            messageCount.incrementAndGet();
        }

        public int getCount() {
            return messageCount.get();
        }
    }

    private static class CountingMessageSource extends MessageCounter implements SensorMessageSourceFactory {
        @Override
        public SensorMessageSource createForSensorType(SensorType sensorType) {
            return () -> Flux.fromStream(
                    IntStream.range(0, MESSAGES_PER_SENSOR_TYPE)
                            .mapToObj(i -> new SensorMessage(
                                    new InetSocketAddress(0), Instant.EPOCH, sensorType, null
                            ))
                    ).doOnNext(message -> increment());
        }
    }

    private static class CountingMessagePublisher extends MessageCounter implements MessagePublisher {
        @Override
        public Mono<Void> publish(Flux<OutboundMessage> messages) {
            return messages.doOnNext(message -> increment()).then();
        }
    }

    private static class FailingCountingMessagePublisher extends CountingMessagePublisher {
        @Override
        public Mono<Void> publish(Flux<OutboundMessage> messages) {
            return Mono.defer(() -> {
                increment();
                return messages
                        .doOnNext(message -> {
                            throw new RuntimeException();
                        })
                        .then();
            });
        }
    }

    @Test
    void testHappyPath() {
        final CountingMessageSource countingMessageSource = new CountingMessageSource();
        final CountingMessagePublisher countingMessagePublisher = new CountingMessagePublisher();

        final SensorMessagePipeline sensorMessagePipeline = new SensorMessagePipeline(
                countingMessageSource,
                new DefaultSensorMessageProcessor(
                        mockDeserializer,
                        mockOutboundMessageWriter,
                        mockGeneralConfigProvider
                ),
                countingMessagePublisher,
                retryPolicy
        );

        sensorMessagePipeline.runPipeline();

        assertEquals(expectedMessageCount, countingMessageSource.getCount());
        assertEquals(expectedMessageCount, countingMessagePublisher.getCount());
    }

    @Test
    void testMessagesSkippedWhenDeserializationFails() {
        final CountingMessageSource countingMessageSource = new CountingMessageSource();
        final CountingMessagePublisher countingMessagePublisher = new CountingMessagePublisher();

        final SensorMessagePipeline sensorMessagePipeline = new SensorMessagePipeline(
                countingMessageSource,
                new DefaultSensorMessageProcessor(
                        failingDeserializer,
                        mockOutboundMessageWriter,
                        mockGeneralConfigProvider
                ),
                countingMessagePublisher,
                retryPolicy
        );

        assertDoesNotThrow(sensorMessagePipeline::runPipeline);

        assertEquals(expectedMessageCount, countingMessageSource.getCount());
        assertEquals(0, countingMessagePublisher.getCount());
    }

    @Test
    void testMessagesSkippedWhenConversionFails() {
        final CountingMessageSource countingMessageSource = new CountingMessageSource();
        final CountingMessagePublisher countingMessagePublisher = new CountingMessagePublisher();

        final SensorMessagePipeline sensorMessagePipeline = new SensorMessagePipeline(
                countingMessageSource,
                new DefaultSensorMessageProcessor(
                        mockDeserializer,
                        failingOutboundMessageWriter,
                        mockGeneralConfigProvider
                ),
                countingMessagePublisher,
                retryPolicy
        );

        assertDoesNotThrow(sensorMessagePipeline::runPipeline);

        assertEquals(expectedMessageCount, countingMessageSource.getCount());
        assertEquals(0, countingMessagePublisher.getCount());
    }

    @Test
    void testMessagesSkippedWhenPublicationFails() {
        final CountingMessageSource countingMessageSource = new CountingMessageSource();
        final FailingCountingMessagePublisher failingCountingMessagePublisher = new FailingCountingMessagePublisher();

        final SensorMessagePipeline sensorMessagePipeline = new SensorMessagePipeline(
                countingMessageSource,
                new DefaultSensorMessageProcessor(
                        mockDeserializer,
                        mockOutboundMessageWriter,
                        mockGeneralConfigProvider
                ),
                failingCountingMessagePublisher,
                retryPolicy
        );

        assertThrows(IllegalStateException.class, sensorMessagePipeline::runPipeline);

        assertEquals(1, countingMessageSource.getCount());
        assertEquals(MAX_RETRIES + 1, failingCountingMessagePublisher.getCount());
    }

}