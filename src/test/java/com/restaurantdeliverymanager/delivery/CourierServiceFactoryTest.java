package com.restaurantdeliverymanager.delivery;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.restaurantdeliverymanager.models.DeliverableShelfItem;
import com.restaurantdeliverymanager.models.ShelfRemoveManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)

public class CourierServiceFactoryTest {

    @Mock
    ShelfRemoveManager manager;

    @Mock
    DeliverableShelfItem item;

    @Test
    public void testCourierServiceCreation() {
        CourierServiceFactory factory =new CourierServiceFactory();
        CourierService courier1 = factory.getOne(manager, item);
        CourierService courier2 = factory.getOne(manager, item);
        assertNotEquals(courier1, courier2);

    }

}
