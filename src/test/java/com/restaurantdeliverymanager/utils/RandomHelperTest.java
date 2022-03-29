package com.restaurantdeliverymanager.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class RandomHelperTest {

    @Test
    public void checkRandom(){
        RandomHelper helper = new RandomHelper();
        int value = helper.getRandomNumberBetween(2, 2);
        assertEquals(2, value);
        value = helper.getRandomNumberBetween(2, 100);
        assertTrue(value<=100);
        assertTrue(value>=2);
    }

}
