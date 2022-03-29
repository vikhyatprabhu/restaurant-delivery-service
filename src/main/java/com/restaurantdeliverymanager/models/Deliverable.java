package com.restaurantdeliverymanager.models;

/**
 * Interface that exposes related to parameters that affect delivery
 */
public interface Deliverable {

    /**
     * Check if item has expired
     *
     * @return true if expired , else false
     */
    boolean hasExpired();

    /**
     * Set the delivery state
     */
    void setDeliveryState(OrderState state);

    /**
     * Get delivery state of the item
     * @return order state
     */
    OrderState getDeliveryState();

}
