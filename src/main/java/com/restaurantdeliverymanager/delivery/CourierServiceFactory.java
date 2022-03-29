package com.restaurantdeliverymanager.delivery;

import com.restaurantdeliverymanager.models.DeliverableShelfItem;
import com.restaurantdeliverymanager.models.ShelfRemoveManager;

public class CourierServiceFactory {

    public CourierService getOne(ShelfRemoveManager shelfManager ,DeliverableShelfItem shelfItem ){
        return new CourierService(shelfManager, shelfItem);
    }
}
