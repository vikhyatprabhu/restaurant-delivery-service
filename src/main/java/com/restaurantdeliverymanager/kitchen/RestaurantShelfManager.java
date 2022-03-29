package com.restaurantdeliverymanager.kitchen;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.restaurantdeliverymanager.models.Property;
import com.restaurantdeliverymanager.models.Shelf;
import com.restaurantdeliverymanager.models.ShelfItem;
import com.restaurantdeliverymanager.models.ShelfTemperature;
import com.restaurantdeliverymanager.models.exception.ItemExistsException;
import com.restaurantdeliverymanager.models.exception.WrongShelfException;
import com.restaurantdeliverymanager.utils.OrderServiceConstants;
import com.restaurantdeliverymanager.utils.logging.ConsoleLogger;

class RestaurantShelfManager implements ShelfManager {

    public AtomicInteger addCounter = new AtomicInteger(0);

    private Map<String, Shelf<ShelfItem, Property>> shelfMap;

    public RestaurantShelfManager() {
        this.shelfMap = new HashMap<>();
        this.shelfMap.put(ShelfTemperature.HOT.getLabel(),
                new RestaurantShelf(OrderServiceConstants.NORMAL_SHELF_CAPACITY, ShelfTemperature.HOT));
        this.shelfMap.put(ShelfTemperature.COLD.getLabel(),
                new RestaurantShelf(OrderServiceConstants.NORMAL_SHELF_CAPACITY, ShelfTemperature.COLD));
        this.shelfMap.put(ShelfTemperature.FROZEN.getLabel(),
                new RestaurantShelf(OrderServiceConstants.NORMAL_SHELF_CAPACITY, ShelfTemperature.FROZEN));
        this.shelfMap.put(ShelfTemperature.OVERFLOW.getLabel(),
                new RestaurantShelf(OrderServiceConstants.OVERFLOW_SHELF_CAPACITY, ShelfTemperature.OVERFLOW));
    }

    @Override
    public void addOrderToShelf(ShelfItem order) throws ItemExistsException, WrongShelfException {
        Shelf<ShelfItem, Property> shelfToAdd = this.shelfMap.get(order.getPropertyString());
        synchronized (this) {
            if (!shelfToAdd.isFull()) {
                ConsoleLogger.getInstance().log("Adding to :" + shelfToAdd.getProperty());
                shelfToAdd.add(order);
            } else {
                this.addToOverFlowQueue(order);
            }
            addCounter.getAndIncrement();
        }
    }

    private void addToOverFlowQueue(ShelfItem order) throws ItemExistsException, WrongShelfException {
        if (this.shelfMap.get(ShelfTemperature.OVERFLOW.getLabel()).isFull()) {
            ConsoleLogger.getInstance().log("OVERFLOW QUEUE WAS FULL while adding : \n " + order.toString());
            boolean itemRemoved = moveOrderToOtherQueueIfFree();
            if (!itemRemoved) {
                removeRandomOrderFromOverFlow();
            }
        }
        ConsoleLogger.getInstance()
                .log("Adding to :" + this.shelfMap.get(ShelfTemperature.OVERFLOW.getLabel()).getProperty());
        this.shelfMap.get(ShelfTemperature.OVERFLOW.getLabel()).add(order);
        ConsoleLogger.getInstance().log("##### AFTER ######");
        this.printShelveContents();
    }

    private boolean moveOrderToOtherQueueIfFree() throws ItemExistsException, WrongShelfException {

        Set<String> checkedQueues = new HashSet<>();
        for (ShelfItem overflowListOrder: this.shelfMap.get(ShelfTemperature.OVERFLOW.getLabel()).getElements()) {
            if (checkedQueues.contains(overflowListOrder.getPropertyString())) {
                continue;
            }
            checkedQueues.add(overflowListOrder.getPropertyString());
            ConsoleLogger.getInstance().log("Checking if " + overflowListOrder.getPropertyString() + ": "
                    + overflowListOrder.toString() + " queue if full");
            ConsoleLogger.getInstance().log("Queue Status " + overflowListOrder.getPropertyString() + ": "
                    + this.shelfMap.get(overflowListOrder.getPropertyString()).isFull());
            if (!this.shelfMap.get(overflowListOrder.getPropertyString()).isFull()) {
                this.shelfMap.get(ShelfTemperature.OVERFLOW.getLabel()).remove(overflowListOrder);
                ConsoleLogger.getInstance().log("Moving to QUEUE " + overflowListOrder.getPropertyString());
                return this.shelfMap.get(overflowListOrder.getPropertyString()).add(overflowListOrder);

            }

        }
        return false;
    }

    private void removeRandomOrderFromOverFlow() {
        this.shelfMap.get(ShelfTemperature.OVERFLOW.getLabel()).getElements().stream().findAny().ifPresent(o -> {
            ConsoleLogger.getInstance().log("##### BEFORE ####");
            this.printShelveContents();
            ConsoleLogger.getInstance().log("Removing random element " + o.toString());
            this.shelfMap.get(ShelfTemperature.OVERFLOW.getLabel()).remove(o);
        });
    }

    /**
     * New Line separated Shelf Summary
     *
     * @return
     */
    public String getShelvesSummary() {
        StringBuilder tableSummary = new StringBuilder();
        this.shelfMap.forEach((key, shelf) -> tableSummary.append(shelf.getSummary()).append("\n"));
        return tableSummary.toString();
    }

    private void printShelveContents() {
        ConsoleLogger.getInstance().log(getShelvesSummary());
    }

    @Override
    public boolean removeFromShelf(ShelfItem order) {
        this.printShelveContents();
        Shelf<ShelfItem, Property> expectedShelf = this.shelfMap.get(order.getPropertyString());

        synchronized (this) {
            if (expectedShelf.contains(order)) {
                return expectedShelf.remove(order);
            } else {
                if (this.shelfMap.get(ShelfTemperature.OVERFLOW.getLabel()).contains(order)) {
                    return this.shelfMap.get(ShelfTemperature.OVERFLOW.getLabel()).remove(order);
                }
                return false;
            }

        }

    }

}
