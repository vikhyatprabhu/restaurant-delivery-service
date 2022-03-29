package com.restaurantdeliverymanager.threading;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

public class IngesterTest {

    private void sleeper(int seconds) {
        new SleepHelper().forDuration(seconds);
    }

    private void sleeper(List<Integer> seconds) {
        new Ingester().ingestAList(seconds, this::sleeper);
    }

    @Test
    public void ingestAList() {
        Ingester ingester = new Ingester();
        long start = System.currentTimeMillis();
        ingester.<Integer>ingestAList(List.of(1, 2, 3, 4), this::sleeper);
        long end = System.currentTimeMillis();
        assertTrue(10 > (end - start) / 1000);
    }

    @Test
    public void ingestInterruptNoException() {
        Ingester ingester = new Ingester();
        Thread t = new Thread(() -> {
            ingester.<Integer>ingestAList(List.of(1, 2, 3, 4), this::sleeper);
        });
        t.start();
        t.interrupt();
        assertTrue(true);

    }

    @Test
    public void ingestAListWithDelayTest() {
        Ingester ingester = new Ingester();
        long start = System.currentTimeMillis();
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        int batchSize = 3;
        int expected = Collections.max(list) + (list.size() / batchSize) + 1;
        ingester.ingestAListWithDelay(list, batchSize, this::sleeper, 1);
        long end = System.currentTimeMillis();
        assertTrue(expected >= (end - start) / 1000);
    }

}
