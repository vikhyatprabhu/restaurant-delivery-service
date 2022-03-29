package com.restaurantdeliverymanager.utils;


public class RandomHelper {

    /**
     * Gets a random integer between minimum and maximum
     * @param minimum
     * @param maximum
     * @return random integer
     */
    public int getRandomNumberBetween(int minimum, int maximum) {
        return (int) (Math.random() * (maximum - minimum)) + minimum;
    }

}
