package com.restaurantdeliverymanager.kitchen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import com.restaurantdeliverymanager.models.Order;
import com.restaurantdeliverymanager.models.OrderState;
import com.restaurantdeliverymanager.models.ShelfTemperature;
import com.restaurantdeliverymanager.models.exception.WrongShelfException;
import com.restaurantdeliverymanager.threading.SleepHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test for {@link RestaurantOrder}
 */
class RestaurantOrderTest {

    private RestaurantOrder order;
    private RestaurantOrder longOrder;


    @BeforeEach
    public void testSetup() {
        Date createdTime = new Date();
        order = (RestaurantOrder) RestaurantOrder.of(createOrder(), createdTime);
        longOrder = (RestaurantOrder) RestaurantOrder.of(createLongOrder(), createdTime);
    }

    private Order createOrder() {
        Order order = new Order();
        order.setDecayRate(0.5);
        order.setId("12edcf");
        order.setName("A");
        order.setShelfLife(4);
        order.setTemp("hot");
        return order;
    }

    private Order createLongOrder() {
        Order order2 = new Order();
        order2.setDecayRate(0.2);
        order2.setId("12edcf");
        order2.setName("A");
        order2.setShelfLife(20);
        order2.setTemp("cold");
        return order2;
    }

    private double roundOff(double value, int places) {
        double multiplier = Math.pow(10.0, places);
        return Math.round(value * multiplier) / multiplier;
    }

    private double getValue(int shelfLife, long age, int shelfDecayModifier, double decayRate) {
        return (shelfLife - age - age * decayRate * shelfDecayModifier) / shelfLife;
    }

    /**
     * Check when an order is expired after its time has elapsed.
     *
     * @throws WrongShelfException
     */
    @Test
    void testOrderAgeExpired() throws WrongShelfException {
        order.addedToShelf(ShelfTemperature.OVERFLOW);
        new SleepHelper().forDuration(3);
        assertTrue(order.hasExpired());
    }

    /**
     * Check that order has not expired when its shelf life is not done.
     *
     * @throws WrongShelfException
     */
    @Test
    void testOrderAgeNotExpired() throws WrongShelfException {
        order.addedToShelf(ShelfTemperature.OVERFLOW);
        new SleepHelper().forDuration(1);
        assertFalse(order.hasExpired());
    }

    /**
     * Check value change when order changed from overflow to normal shelf
     *
     * @throws WrongShelfException
     */
    @Test
    void testOrderAgeShelfChangeValue() throws WrongShelfException {
        longOrder.addedToShelf(ShelfTemperature.OVERFLOW);
        new SleepHelper().forDuration(2);
        double expectedAge = getValue(longOrder.getOrder().getShelfLife(), 2, ShelfTemperature.OVERFLOW.getValue(),
                longOrder.getOrder().getDecayRate());
        double value = roundOff(longOrder.getValue(), 2);
        longOrder.addedToShelf(ShelfTemperature.COLD);
        new SleepHelper().forDuration(2);
        double expectedAge2 = getValue(longOrder.getOrder().getShelfLife(), 2, ShelfTemperature.COLD.getValue(),
                longOrder.getOrder().getDecayRate()) - (1.0 - value);
        double secondValue = roundOff(longOrder.getValue(), 2);
        longOrder.setDeliveryState(OrderState.DELIVERED);
        assertEquals(roundOff(expectedAge, 2), value);
        assertEquals(roundOff(expectedAge2, 2), secondValue);
        new SleepHelper().forDuration(2);
        assertEquals(roundOff(expectedAge2, 2), roundOff(longOrder.getValue(), 2));
    }

    /**
     * Check value change when order changed from normal to overflow Although this
     * situation doesnt arise in the application
     *
     * @throws WrongShelfException
     */
    @Test
    void testOrderAgeShelfChangeShelfOrderReversed() throws WrongShelfException {
        longOrder.addedToShelf(ShelfTemperature.COLD);
        new SleepHelper().forDuration(2);
        double expectedAge = getValue(longOrder.getOrder().getShelfLife(), 2, ShelfTemperature.COLD.getValue(),
                longOrder.getOrder().getDecayRate());
        double value = roundOff(longOrder.getValue(), 2);
        longOrder.addedToShelf(ShelfTemperature.OVERFLOW);
        new SleepHelper().forDuration(2);
        double expectedAge2 = getValue(longOrder.getOrder().getShelfLife(), 2, ShelfTemperature.OVERFLOW.getValue(),
                longOrder.getOrder().getDecayRate()) - (1.0 - value);
        double secondValue = roundOff(longOrder.getValue(), 2);
        assertEquals(roundOff(expectedAge, 2), value);
        assertEquals(roundOff(expectedAge2, 2), secondValue);

    }

    @Test
    void testShelfChangeAlwaysDecreasing() throws WrongShelfException {
        longOrder.addedToShelf(ShelfTemperature.OVERFLOW);
        new SleepHelper().forDuration(1);
        double value = longOrder.getValue();
        longOrder.addedToShelf(ShelfTemperature.COLD);
        new SleepHelper().forDuration(1);
        double secondValue = longOrder.getValue();
        assertTrue(secondValue <= value);
        new SleepHelper().forDuration(1);
        double thirdValue = longOrder.getValue();
        assertTrue(thirdValue <= secondValue);
    }

    @Test
    void testSetTemperature() throws WrongShelfException {
        longOrder.addedToShelf(ShelfTemperature.OVERFLOW);
        new SleepHelper().forDuration(1);
        double value = longOrder.getValue();
        longOrder.addedToShelf(ShelfTemperature.COLD);
        new SleepHelper().forDuration(1);
        double secondValue = longOrder.getValue();
        assertTrue(secondValue <= value);
        new SleepHelper().forDuration(1);
        double thirdValue = longOrder.getValue();
        assertTrue(thirdValue <= secondValue);
    }

    @Test
    public void testOrderState() {
        longOrder.setDeliveryState(OrderState.DELIVERED);
        assertEquals(OrderState.DELIVERED, longOrder.getDeliveryState());
        assertEquals(longOrder.getPropertyString(), ShelfTemperature.COLD.getLabel());
    }

    @Test
    public void testOrderWrongShelf() {
        assertThrows(WrongShelfException.class, () -> longOrder.addedToShelf(ShelfTemperature.HOT));
    }

    @Test
    public void testOrderString(){
        assertEquals(order.toString(),"12edcf|A|hot|1.0");
    }

}
