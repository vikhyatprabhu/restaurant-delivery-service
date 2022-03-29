package com.restaurantdeliverymanager.delivery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.restaurantdeliverymanager.models.DeliverableShelfItem;
import com.restaurantdeliverymanager.models.OrderState;
import com.restaurantdeliverymanager.models.ShelfRemoveManager;
import com.restaurantdeliverymanager.threading.SleepHelper;
import com.restaurantdeliverymanager.utils.OrderServiceConstants;
import com.restaurantdeliverymanager.utils.RandomHelper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CourierServiceTest {

    @Mock
    private SleepHelper sleepHelper;

    @Mock
    private ShelfRemoveManager removeManager;

    @Mock
    private DeliverableShelfItem shelfItem;

    @Mock
    private RandomHelper randomHelper;

    @InjectMocks
    private CourierService service = new CourierService(removeManager, shelfItem);



    @Test
    public void testCourierDeliveryFailed() {

        when(removeManager.removeFromShelf(shelfItem)).thenReturn(false);
        doReturn(2).when(randomHelper).getRandomNumberBetween(OrderServiceConstants.DELIVERY_MIN_DELAY,
                OrderServiceConstants.DELIVERY_MAX_DELAY);
        doNothing().when(sleepHelper).forDuration(2);
        OrderState orderState = service.deliverOrder();
        verify(removeManager, times(1)).removeFromShelf(shelfItem);
        assertEquals(orderState, OrderState.DISCARDED);
    }

    @Test
    public void testCourierDeliverySuccess() {

        when(removeManager.removeFromShelf(shelfItem)).thenReturn(true);
        doReturn(2).when(randomHelper).getRandomNumberBetween(OrderServiceConstants.DELIVERY_MIN_DELAY,
                OrderServiceConstants.DELIVERY_MAX_DELAY);
        doNothing().when(sleepHelper).forDuration(2);
        doReturn(false).when(shelfItem).hasExpired();
        OrderState orderState = service.deliverOrder();
        verify(removeManager, times(1)).removeFromShelf(shelfItem);
        assertEquals(orderState, OrderState.DELIVERED);
    }

    @Test
    public void testCourierDeliveryExpired() {

        when(removeManager.removeFromShelf(shelfItem)).thenReturn(true);
        doReturn(2).when(randomHelper).getRandomNumberBetween(OrderServiceConstants.DELIVERY_MIN_DELAY,
                OrderServiceConstants.DELIVERY_MAX_DELAY);
        doNothing().when(sleepHelper).forDuration(2);
        doReturn(true).when(shelfItem).hasExpired();
        OrderState orderState = service.deliverOrder();
        verify(removeManager, times(1)).removeFromShelf(shelfItem);
        assertEquals(orderState, OrderState.EXPIRED);
    }

}
