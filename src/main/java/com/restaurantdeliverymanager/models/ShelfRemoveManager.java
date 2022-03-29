package com.restaurantdeliverymanager.models;

/**
 *
 */
public interface ShelfRemoveManager {

    /**
     * Remove an item from a shelf
     * @param deliverable
     * @return if the item is removed or not
     */
    boolean removeFromShelf(ShelfItem deliverable);
}
