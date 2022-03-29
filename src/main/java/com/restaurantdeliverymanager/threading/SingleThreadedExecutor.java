package com.restaurantdeliverymanager.threading;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SingleThreadedExecutor<T>  {


    public T executeSingleThread(Callable<T> function) {

        ExecutorService exec = Executors.newSingleThreadExecutor();
        Future<T> future = exec.submit(function);
        T result = null;
        try {
            result = future.get();
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block

        }
        exec.shutdown();
        return result;

    }

}
