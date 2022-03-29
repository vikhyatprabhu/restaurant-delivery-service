package com.restaurantdeliverymanager.kitchen;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import com.restaurantdeliverymanager.delivery.CourierService;
import com.restaurantdeliverymanager.delivery.CourierServiceFactory;
import com.restaurantdeliverymanager.models.Order;
import com.restaurantdeliverymanager.models.OrderState;
import com.restaurantdeliverymanager.models.ShelfItem;
import com.restaurantdeliverymanager.models.exception.ItemExistsException;
import com.restaurantdeliverymanager.models.exception.WrongShelfException;
import com.restaurantdeliverymanager.threading.Ingester;
import com.restaurantdeliverymanager.threading.SingleThreadedExecutor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CloudKitchenTest {

    @Mock
    Order order1;

    @Mock
    Order order2;

    @Mock
    Ingester ingester;

    @Mock
    private CourierServiceFactory courierServiceFactory;

    @Mock
    private SingleThreadedExecutor<OrderState> executor;

    @Mock
    CourierService courier;

    @Mock
    ShelfManager shelfManager;

    @Mock
    private List<ShelfItem> expiredOrders;

    @Mock
    private List<ShelfItem> deliveredOrders;

    @Mock
    private List<ShelfItem> discardedOrders;

    @InjectMocks
    Kitchen kitchen = new Kitchen();

    private Order createLongOrder() {
        Order order1 = new Order();
        order1.setDecayRate(0.5);
        order1.setId("sdascax");
        order1.setName("A");
        order1.setShelfLife(20);
        order1.setTemp("hot");
        return order1;
    }

    private Order createShortOrder() {
        Order order1 = new Order();
        order1.setDecayRate(0.6);
        order1.setId("asdacd");
        order1.setName("A");
        order1.setShelfLife(2);
        order1.setTemp("cold");
        return order1;
    }

    @Test
    public void testIngesterCalled() {
        List<Order> orders = Arrays.asList(order1, order2);
        kitchen.processOrders(orders);
        verify(ingester, times(1)).ingestAList(eq(orders), any());
    }

    @Test
    public void testProcessOrders() throws ItemExistsException, WrongShelfException {
        Order order1 = createLongOrder();
        Order order2 = createShortOrder();
        List<Order> orders = Arrays.asList(order1,order2);
        doCallRealMethod().when(ingester).ingestAList(eq(orders), any());
        doCallRealMethod().when(executor).executeSingleThread(any());
        doReturn(courier).when(courierServiceFactory).getOne(eq(shelfManager), any());
        doReturn(OrderState.DELIVERED).when(courier).deliverOrder();
        kitchen.processOrders(orders);
        verify(shelfManager , times(orders.size())).addOrderToShelf(any());
        verify(deliveredOrders, times(2)).add(any());

    }

    @Test
    public void testOrderProcessExpired() throws ItemExistsException, WrongShelfException{
        Order order1 = createLongOrder();
        List<Order> orders = Arrays.asList(order1);
        doCallRealMethod().when(ingester).ingestAList(eq(orders), any());
        doCallRealMethod().when(executor).executeSingleThread(any());
        doReturn(courier).when(courierServiceFactory).getOne(eq(shelfManager), any());
        doReturn(OrderState.EXPIRED).when(courier).deliverOrder();
        kitchen.processOrders(orders);
        verify(shelfManager , times(orders.size())).addOrderToShelf(any());
        verify(expiredOrders, times(1)).add(any());


    }

    @Test
    public void testOrderProcessDiscarded() throws ItemExistsException, WrongShelfException{
        Order order1 = createLongOrder();
        List<Order> orders = Arrays.asList(order1);
        doCallRealMethod().when(ingester).ingestAList(eq(orders), any());
        doCallRealMethod().when(executor).executeSingleThread(any());
        doReturn(courier).when(courierServiceFactory).getOne(eq(shelfManager), any());
        doReturn(OrderState.DISCARDED).when(courier).deliverOrder();
        kitchen.processOrders(orders);
        verify(shelfManager , times(orders.size())).addOrderToShelf(any());
        verify(discardedOrders, times(1)).add(any());
    }



}
