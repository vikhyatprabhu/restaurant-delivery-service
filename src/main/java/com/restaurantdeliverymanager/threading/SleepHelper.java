package com.restaurantdeliverymanager.threading;

import com.restaurantdeliverymanager.utils.logging.ConsoleLogger;

public class SleepHelper {

    public void forDuration(long seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            ConsoleLogger.getInstance().log("Thread interrupted:" + e.getMessage());
        }
    }

}
