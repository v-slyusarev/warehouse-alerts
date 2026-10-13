package com.github.vslyusarev.warehousealerts.central.application.state;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class InMemorySensorsStateTest {
    private static final String WAREHOUSE_ID = "w1";
    private static final String OTHER_WAREHOUSE_ID = "w2";
    private static final String SENSOR_ID = "t1";
    private static final String OTHER_SENSOR_ID = "h1";

    @Test
    void testNoPreviousState() {
        final InMemorySensorsState sensorsState = new InMemorySensorsState();
        assertTrue(sensorsState.getAndUpdateState(WAREHOUSE_ID, SENSOR_ID, 0).isEmpty());
    }

    @Test
    void testUpdatePreviousState() {
        final InMemorySensorsState sensorsState = new InMemorySensorsState();
        sensorsState.getAndUpdateState(WAREHOUSE_ID, SENSOR_ID, 0);
        assertEquals(Optional.of(0), sensorsState.getAndUpdateState(WAREHOUSE_ID, SENSOR_ID, 1));
        assertEquals(Optional.of(1), sensorsState.getAndUpdateState(WAREHOUSE_ID, SENSOR_ID, 2));
    }

    @Test
    void testDifferentStates() {
        final InMemorySensorsState sensorsState = new InMemorySensorsState();
        sensorsState.getAndUpdateState(WAREHOUSE_ID, SENSOR_ID, 0);
        sensorsState.getAndUpdateState(OTHER_WAREHOUSE_ID, SENSOR_ID, 1);
        sensorsState.getAndUpdateState(WAREHOUSE_ID, OTHER_SENSOR_ID, 2);
        assertEquals(Optional.of(0), sensorsState.getAndUpdateState(WAREHOUSE_ID, SENSOR_ID, 0));
    }

    @Test
    void testThreadSafety() {
        final InMemorySensorsState sensorsState = new InMemorySensorsState();
        sensorsState.getAndUpdateState(WAREHOUSE_ID, SENSOR_ID, -1);

        final int threadCount = Runtime.getRuntime().availableProcessors();
        try (final ExecutorService executorService = Executors.newFixedThreadPool(threadCount)) {
            final CountDownLatch startLatch = new CountDownLatch(1);
            final int updates = 10000;
            final CountDownLatch endLatch = new CountDownLatch(updates);
            final Set<Integer> recordedStates = ConcurrentHashMap.newKeySet();

            for (int updateCounter = 0; updateCounter < updates; ++updateCounter) {
                int nextValue = updateCounter;
                executorService.submit(() -> {
                    try {
                        startLatch.await();
                        recordedStates.add(
                                sensorsState.getAndUpdateState(WAREHOUSE_ID, SENSOR_ID, nextValue) // update multiple times with unique values, record the last value
                                        .orElseThrow());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        endLatch.countDown();
                    }
                });
            }
            startLatch.countDown();
            try {
                endLatch.await();
                executorService.shutdown();
                assertEquals(updates, recordedStates.size()); // assert that no duplicate values were recorded, i.e. each thread saw the previous update
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


}