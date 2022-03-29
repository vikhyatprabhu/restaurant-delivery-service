package com.restaurantdeliverymanager.models;

import com.restaurantdeliverymanager.models.exception.WrongShelfException;

/**
 * Interface for an Item which can be placed in a shelf typically distinguished
 * by a {@link Property}.
 */
public interface ShelfItem {

    public String getPropertyString();

    public void addedToShelf(Property property) throws WrongShelfException;

}
