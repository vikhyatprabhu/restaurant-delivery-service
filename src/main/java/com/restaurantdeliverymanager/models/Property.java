package com.restaurantdeliverymanager.models;

/**
 * Property is used as categorization for shelf types
 * currently we have ShelfTemperature , we could extend this
 * to have different shelves
 */
public interface Property {

    int getValue();

    String getLabel();

}
