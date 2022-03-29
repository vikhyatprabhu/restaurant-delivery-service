package com.restaurantdeliverymanager.threading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.restaurantdeliverymanager.utils.logging.ConsoleLogger;

/**
 * Class which works with processing a list of items of type T
 */
public class Ingester {

    private final SleepHelper sleepHelper = new SleepHelper();

    /**
     * Ingest a list which needs to be processed with a delay
     *
     * @param <T>           Type of the target object which would be processed
     * @param listToProcess
     * @param ingestionRate
     * @param functionToRun Consumer function which runs
     * @param delay         Delay between processing in seconds
     */
    public <T> void ingestAListWithDelay(List<T> listToProcess, int ingestionRate, Consumer<List<T>> functionToRun,
            int delay) {

        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<?>> futures = new ArrayList<Future<?>>();
        for (int i = 0; i < listToProcess.size(); i += ingestionRate) {
            final int j = i;
            ConsoleLogger.getInstance().log("Processing j: " + j + " to " + (j + ingestionRate) + "\n");
            Future<?> future= executor.submit(() -> {
                int processingListSize = j + ingestionRate;
                if ((j + ingestionRate) >= listToProcess.size()) {
                    processingListSize = j + (listToProcess.size() - j);
                }
                List<T> ordersToProcess = listToProcess.subList(j, processingListSize);

                functionToRun.accept(ordersToProcess);
            });
            futures.add(future);
            sleepHelper.forDuration(delay);

        }
        waitForCompletion(futures);
        executor.shutdown();
    }

    public <T> void ingestAList(List<T> items, Consumer<T> consumer) {
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<?>> futures = items.stream().map(item -> executor.submit(() -> consumer.accept(item)))
                .collect(Collectors.toList());
        waitForCompletion(futures);
        executor.shutdown();

    }

    private void waitForCompletion(List<Future<?>> futures) {
        futures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                ConsoleLogger.getInstance().log("Thread exception" + e.getMessage());
            }
        });
    }
}
