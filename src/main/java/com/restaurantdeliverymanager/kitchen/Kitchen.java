package com.restaurantdeliverymanager.kitchen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.restaurantdeliverymanager.delivery.CourierService;
import com.restaurantdeliverymanager.delivery.CourierServiceFactory;
import com.restaurantdeliverymanager.models.DeliverableShelfItem;
import com.restaurantdeliverymanager.models.Order;
import com.restaurantdeliverymanager.models.OrderState;
import com.restaurantdeliverymanager.models.ShelfItem;
import com.restaurantdeliverymanager.models.exception.ItemExistsException;
import com.restaurantdeliverymanager.models.exception.WrongShelfException;
import com.restaurantdeliverymanager.threading.Ingester;
import com.restaurantdeliverymanager.threading.SingleThreadedExecutor;
import com.restaurantdeliverymanager.utils.logging.ConsoleLogger;


/**
 * Kitchen class which manages processing of orders.
 */
public class Kitchen {

    private ShelfManager shelfManager;
    private List<ShelfItem> discardedOrders;
    private List<ShelfItem> expiredOrders;
    private List<ShelfItem> deliveredOrders;
    private Ingester ingester ;
    private CourierServiceFactory courierServiceFactory;
    private SingleThreadedExecutor<OrderState> executor;

    public Kitchen() {
        this.shelfManager = new RestaurantShelfManager();
        this.discardedOrders = Collections.synchronizedList(new ArrayList<>());
        this.expiredOrders = Collections.synchronizedList(new ArrayList<>());
        this.deliveredOrders = Collections.synchronizedList(new ArrayList<>());
        this.ingester = new Ingester();
        this.courierServiceFactory = new CourierServiceFactory();
        this.executor = new SingleThreadedExecutor<>();
    }

    /**
     * Process a list of orders in kitchen and trigger delivery for the same.
     *
     * @param orders List of orders to be processed
     */
    public void processOrders(List<Order> orders) {
        ingester.ingestAList(orders, this::processOrder);
        this.printOrders();
    }

    private void processOrder(Order order) {
        ConsoleLogger.getInstance().log("Processed order:" + order.getName() + ":" + order.getId() + "\n");
        DeliverableShelfItem kitchenOrder = RestaurantOrder.of(order, new Date());
        kitchenOrder.setDeliveryState(OrderState.PROCESSING);
        try {
            shelfManager.addOrderToShelf(kitchenOrder);
        } catch (ItemExistsException | WrongShelfException e) {
            ConsoleLogger.getInstance().log(e.getMessage());
        }

        kitchenOrder.setDeliveryState(OrderState.WAITING_FOR_COURIER);

        OrderState orderState = this.executor.executeSingleThread(() -> {
            CourierService courier = this.courierServiceFactory.getOne(shelfManager, kitchenOrder);
            return courier.deliverOrder();
        });

        kitchenOrder.setDeliveryState(orderState);

        if (kitchenOrder.getDeliveryState().equals(OrderState.DISCARDED)) {
            this.discardedOrders.add(kitchenOrder);
        } else if (kitchenOrder.getDeliveryState().equals(OrderState.DELIVERED)) {
            this.deliveredOrders.add(kitchenOrder);
        } else if (kitchenOrder.getDeliveryState().equals(OrderState.EXPIRED)) {
            this.expiredOrders.add(kitchenOrder);
        }

    }

    public void printOrders() {
        ConsoleLogger.getInstance().log("Discarded Orders Size:  " + discardedOrders.size());
        this.discardedOrders.forEach(order -> ConsoleLogger.getInstance().log(order.toString()));
        ConsoleLogger.getInstance().log("Expired orders Size: " + this.expiredOrders.size());
        this.expiredOrders.forEach(order -> ConsoleLogger.getInstance().log(order.toString()));

    }

}
