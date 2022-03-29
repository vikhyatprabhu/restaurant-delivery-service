package com.restaurantdeliverymanager;

import java.util.ArrayList;
import java.util.List;


import com.restaurantdeliverymanager.kitchen.Kitchen;
import com.restaurantdeliverymanager.models.Order;
import com.restaurantdeliverymanager.threading.Ingester;
import com.restaurantdeliverymanager.utils.OrderInitializer;
import com.restaurantdeliverymanager.utils.OrderServiceConstants;
import com.restaurantdeliverymanager.utils.data.DataConfig;
import com.restaurantdeliverymanager.utils.data.FileReader;
import com.restaurantdeliverymanager.utils.logging.ConsoleLogger;
import com.restaurantdeliverymanager.utils.objectmapping.JsonObjectMapper;
import com.restaurantdeliverymanager.utils.objectmapping.ObjectMappingException;

/**
 * Hello world!
 */
public final class RestaurantOrderEmulator {
    private RestaurantOrderEmulator() {
    }

    /**
     * Driver program.
     *
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        OrderInitializer orderInitializer = new OrderInitializer(new JsonObjectMapper(new FileReader()));
        final Kitchen kitchen = new Kitchen();
        List<Order> orders = new ArrayList<>();
        try {
            orders = orderInitializer.getOrders(new DataConfig(OrderServiceConstants.ORDERS_FILE));
        } catch (ObjectMappingException e) {
            ConsoleLogger.getInstance().log("Order initialization failed \n");
        }

        new Ingester().<Order>ingestAListWithDelay(orders, OrderServiceConstants.INGESTION_RATE,kitchen::processOrders, 1);


    }
}
