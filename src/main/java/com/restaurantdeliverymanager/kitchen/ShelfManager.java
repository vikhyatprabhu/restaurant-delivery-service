package com.restaurantdeliverymanager.kitchen;

import com.restaurantdeliverymanager.models.ShelfItem;
import com.restaurantdeliverymanager.models.ShelfRemoveManager;
import com.restaurantdeliverymanager.models.exception.ItemExistsException;
import com.restaurantdeliverymanager.models.exception.WrongShelfException;

interface ShelfManager extends ShelfRemoveManager {

    /**
     * Add an item to a shelf
     *
     * @param item
     */
    void addOrderToShelf(ShelfItem item) throws ItemExistsException, WrongShelfException;

}
