package com.restaurantdeliverymanager.models.exception;

public class ItemExistsException extends Exception {

    private static final long serialVersionUID = 2051356637422383873L;

    public ItemExistsException(String message) {
        super(message);
    }
}
