package com.restaurantdeliverymanager.delivery;

import com.restaurantdeliverymanager.models.DeliverableShelfItem;
import com.restaurantdeliverymanager.models.OrderState;
import com.restaurantdeliverymanager.models.ShelfRemoveManager;
import com.restaurantdeliverymanager.threading.SleepHelper;
import com.restaurantdeliverymanager.utils.OrderServiceConstants;
import com.restaurantdeliverymanager.utils.RandomHelper;
import com.restaurantdeliverymanager.utils.logging.ConsoleLogger;

public class CourierService {

    private ShelfRemoveManager shelfManager;
    private DeliverableShelfItem deliverable;
    private RandomHelper randomHelper = new RandomHelper();
    private SleepHelper sleepHelper = new SleepHelper();

    CourierService(ShelfRemoveManager shelfManager, DeliverableShelfItem deliverable) {
        this.shelfManager = shelfManager;
        this.deliverable = deliverable;
    }

    /**
     * Randomly sleep for a given time interval and then deliver the deliverable by
     * removing it from the shelf manager queues
     *
     * @return Returns {@link OrderState.DELIVERED} if successfully delivered or
     *         {@link OrderState.DISCARDED} if the deliverable is not found on the
     *         shelf or {@link OrderState.EXPIRED} if the value of the deliverable
     *         has reached zero.
     */
    public OrderState deliverOrder() {

        int sleepDuration = randomHelper.getRandomNumberBetween(OrderServiceConstants.DELIVERY_MIN_DELAY,
                OrderServiceConstants.DELIVERY_MAX_DELAY);

        ConsoleLogger.getInstance()
                .log("Delivery will take " + sleepDuration + " seconds for deliverable:" + deliverable.toString());


        sleepHelper.forDuration(sleepDuration);
        boolean successful = shelfManager.removeFromShelf(deliverable);

        if (!successful) {
            ConsoleLogger.getInstance().log("Order Not Found in Shelf:" + deliverable.toString());
            return OrderState.DISCARDED;
        }
        if (deliverable.hasExpired()) {
            ConsoleLogger.getInstance().log("Shelf life expired for Order:" + deliverable);
            return OrderState.EXPIRED;
        }

        ConsoleLogger.getInstance().log("Delivered Successfully: " + deliverable.toString());
        return OrderState.DELIVERED;
    }

}
