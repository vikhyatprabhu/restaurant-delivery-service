package com.restaurantdeliverymanager.kitchen;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.restaurantdeliverymanager.models.Property;
import com.restaurantdeliverymanager.models.Shelf;
import com.restaurantdeliverymanager.models.ShelfItem;
import com.restaurantdeliverymanager.models.exception.ItemExistsException;
import com.restaurantdeliverymanager.models.exception.WrongShelfException;
import com.restaurantdeliverymanager.utils.logging.ConsoleLogger;

/**
 * Implementation of {@link Shelf} which can add {@link ShelfItem} Each shelf
 * has a unique property which is used to categorize the order
 */
public class RestaurantShelf implements Shelf<ShelfItem, Property> {

    private Property property;
    private Set<ShelfItem> items;
    private int capacity;

    public RestaurantShelf(int capacity, Property property) {
        this.capacity = capacity;
        this.property = property;
        this.items = new LinkedHashSet<>();
    }

    @Override
    public List<ShelfItem> getElements() {
        return this.items.stream().collect(Collectors.toList());
    }

    @Override
    public boolean isFull() {
        return this.remainingCapacity() == 0;
    }

    @Override
    public boolean contains(ShelfItem order) {
        return this.items.contains(order);
    }

    @Override
    public boolean add(ShelfItem order) throws ItemExistsException, WrongShelfException {
        ConsoleLogger.getInstance().log("Adding to " + this.property + " Shelf: " + order.toString());
        if (this.contains(order)) {
            throw new ItemExistsException("Item already exists");
        }

        order.addedToShelf(this.property);
        if (!isFull()) {
            return this.items.add(order);
        }
        return false;
    }

    @Override
    public boolean remove(ShelfItem order) {
        ConsoleLogger.getInstance().log("Removing from " + this.property + " shelf:" + order.toString());
        return this.items.remove(order);
    }

    @Override
    public Property getProperty() {
        return property;
    }

    private int remainingCapacity() {
        return capacity - this.items.size();
    }

    @Override
    public String getSummary() {
        StringBuilder outputString = new StringBuilder();
        outputString.append("#### Shelf " + this.property + " Contents ####### \n");
        outputString.append("Remaining Capacity:" + this.remainingCapacity() + "\n");
        this.items.stream().forEach(order -> outputString.append(order).append("\n"));
        return outputString.toString();
    }

}
