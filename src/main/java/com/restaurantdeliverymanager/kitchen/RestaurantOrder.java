package com.restaurantdeliverymanager.kitchen;

import java.util.Date;

import com.restaurantdeliverymanager.models.DeliverableShelfItem;
import com.restaurantdeliverymanager.models.Order;
import com.restaurantdeliverymanager.models.OrderState;
import com.restaurantdeliverymanager.models.Property;
import com.restaurantdeliverymanager.models.exception.WrongShelfException;

class RestaurantOrder implements DeliverableShelfItem {

    private Order order;
    private Date processedAt;
    private double expiredAlready = 0.0;
    private int currentShelfDecayModifier;
    private final String specialShelf = "overflow";

    private OrderState state;
    private double valueAtDelivery;

    public static DeliverableShelfItem of(Order order, Date date) {
        return new RestaurantOrder(order, date);
    }

    private RestaurantOrder(Order order, Date processedAt) {
        this.order = order;
        this.processedAt = processedAt;
    }

    Order getOrder() {
        return this.order;
    }



    public void addedToShelf(Property property) throws WrongShelfException {

        if (!this.getOrder().getTemp().equals(property.getLabel()) && !this.specialShelf.equals(property.getLabel())) {
            throw new WrongShelfException(
                    "Order was of type:" + this.getOrder().getTemp() + " , added to :" + property.getLabel());
        }
        this.expiredAlready = 1.0 - this.calculateAndReturnValue();
        this.processedAt = new Date();
        this.currentShelfDecayModifier = property.getValue();

    }

    @Override
    public void setDeliveryState(OrderState state) {
        if (!state.equals(OrderState.PROCESSING) && !state.equals(OrderState.WAITING_FOR_COURIER)) {
            this.valueAtDelivery = calculateAndReturnValue();
        }
        this.state = state;
    }

    private double calculateAndReturnValue() {
        if(this.valueAtDelivery != 0.0){
            return this.valueAtDelivery;
        }
        long orderAge = (new Date().getTime() - this.processedAt.getTime()) / 1000;
        double value = (this.order.getShelfLife() - orderAge
                - this.order.getDecayRate() * orderAge * this.currentShelfDecayModifier) / this.order.getShelfLife();

        return value - expiredAlready;
    }

    public boolean hasExpired() {
        return calculateAndReturnValue() <= 0.0;
    }

    public String getPropertyString() {
        return this.order.getTemp();
    }

    public double getValue() {
        return calculateAndReturnValue();
    }

    @Override
    public String toString() {
        return this.order.getId() + "|" + this.order.getName() + "|" + this.order.getTemp() + "|"
                + this.calculateAndReturnValue();
    }

    @Override
    public OrderState getDeliveryState() {
        return state;
    }

}
