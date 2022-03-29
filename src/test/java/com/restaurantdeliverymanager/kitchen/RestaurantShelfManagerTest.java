package com.restaurantdeliverymanager.kitchen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.restaurantdeliverymanager.models.Order;
import com.restaurantdeliverymanager.models.Property;
import com.restaurantdeliverymanager.models.Shelf;
import com.restaurantdeliverymanager.models.ShelfItem;
import com.restaurantdeliverymanager.models.ShelfTemperature;
import com.restaurantdeliverymanager.models.exception.ItemExistsException;
import com.restaurantdeliverymanager.models.exception.WrongShelfException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RestaurantShelfManagerTest {

    @Mock
    private Map<String, Shelf<ShelfItem, Property>> shelfMap;

    @Mock
    RestaurantOrder hotOrder;

    @Mock
    RestaurantOrder coldOrder;

    @InjectMocks
    private ShelfManager manager = new RestaurantShelfManager();

    @Mock
    private Shelf<ShelfItem, Property> hotShelf;

    @Mock
    private Shelf<ShelfItem, Property> coldShelf;

    @Mock
    private Shelf<ShelfItem, Property> overFlowShelf;



    private void initOrders() {
        init();
        doReturn(true).when(hotShelf).isFull();
        List<ShelfItem> items = Arrays.<ShelfItem>asList(coldOrder);
        doReturn(ShelfTemperature.COLD.getLabel()).when(coldOrder).getPropertyString();
        doReturn(overFlowShelf).when(shelfMap).get(ShelfTemperature.OVERFLOW.getLabel());
        doReturn(coldShelf).when(shelfMap).get(ShelfTemperature.COLD.getLabel());
        doReturn(true).when(overFlowShelf).isFull();
        doReturn(items).when(overFlowShelf).getElements();
    }

    private void init() {
        doReturn(ShelfTemperature.HOT.getLabel()).when(hotOrder).getPropertyString();
        doReturn(hotShelf).when(shelfMap).get(ShelfTemperature.HOT.getLabel());
    }

    @Test
    public void testAddToShelfNormal() throws ItemExistsException, WrongShelfException {
        init();
        doReturn(false).when(hotShelf).isFull();
        manager.addOrderToShelf(hotOrder);
        verify(hotShelf, times(1)).add(hotOrder);
        verify(overFlowShelf, times(0)).add(hotOrder);
    }

    @Test
    public void testAddToShelfOverFlowNotFull() throws ItemExistsException, WrongShelfException {
        init();
        doReturn(true).when(hotShelf).isFull();
        doReturn(overFlowShelf).when(shelfMap).get(ShelfTemperature.OVERFLOW.getLabel());
        doReturn(false).when(overFlowShelf).isFull();
        manager.addOrderToShelf(hotOrder);
        verify(overFlowShelf, times(1)).add(hotOrder);
        verify(hotShelf, times(0)).add(hotOrder);
    }

    @Test
    public void testAddToShelfOverFlowFull() throws ItemExistsException, WrongShelfException {
        init();
        doReturn(true).when(hotShelf).isFull();
        doReturn(overFlowShelf).when(shelfMap).get(ShelfTemperature.OVERFLOW.getLabel());
        doReturn(true).when(overFlowShelf).isFull();
        manager.addOrderToShelf(hotOrder);
        verify(overFlowShelf, times(1)).add(hotOrder);
        verify(hotShelf, times(0)).add(hotOrder);

    }

    @Test
    public void testAddToShelfOverFlowFullShelfFree() throws ItemExistsException, WrongShelfException {
        initOrders();
        doReturn(false).when(coldShelf).isFull();
        doReturn(true).when(coldShelf).add(coldOrder);
        manager.addOrderToShelf(hotOrder);
        verify(coldShelf, times(1)).add(coldOrder);
        verify(overFlowShelf, times(1)).remove(coldOrder);
        verify(overFlowShelf, times(1)).add(hotOrder);
        verify(hotShelf, times(0)).add(hotOrder);

    }

    @Test
    public void testAddOverFlowFullNoShelfFree() throws ItemExistsException, WrongShelfException {
        initOrders();
        doReturn(true).when(coldShelf).isFull();
        manager.addOrderToShelf(hotOrder);
        verify(coldShelf, times(0)).add(coldOrder);
        verify(overFlowShelf, times(1)).remove(coldOrder);
        verify(overFlowShelf, times(1)).add(hotOrder);
        verify(hotShelf, times(0)).add(hotOrder);
    }

    @Test
    public void testRemoveFromOriginalShelf() {
        init();
        doReturn(true).when(hotShelf).contains(hotOrder);
        manager.removeFromShelf(hotOrder);
        verify(hotShelf, times(1)).remove(hotOrder);

    }

    @Test
    public void testRemoveFromOverflowShelf() {
        init();
        doReturn(false).when(hotShelf).contains(hotOrder);
        doReturn(overFlowShelf).when(shelfMap).get(ShelfTemperature.OVERFLOW.getLabel());
        doReturn(true).when(overFlowShelf).contains(hotOrder);
        manager.removeFromShelf(hotOrder);
        verify(overFlowShelf, times(1)).remove(hotOrder);
        verify(hotShelf, times(0)).remove(hotOrder);

    }

    @Test
    public void testRemoveFromOverflowShelfNotPresent() {
        init();
        doReturn(false).when(hotShelf).contains(hotOrder);
        doReturn(overFlowShelf).when(shelfMap).get(ShelfTemperature.OVERFLOW.getLabel());
        doReturn(false).when(overFlowShelf).contains(hotOrder);
        assertFalse(manager.removeFromShelf(hotOrder));
        verify(overFlowShelf, times(0)).remove(hotOrder);
        verify(hotShelf, times(0)).remove(hotOrder);

    }

    private Order createLongOrder(String id, String temp) {
        Order order2 = new Order();
        order2.setDecayRate(0.2);
        order2.setId(id);
        order2.setName("A");
        order2.setShelfLife(20);
        order2.setTemp(temp);
        return order2;
    }

    @Test
    public void multiThreadedTest() throws InterruptedException {

        String expected = String.join("\n", "#### Shelf OVERFLOW Contents ####### ", "Remaining Capacity:15\n",
                "#### Shelf FROZEN Contents ####### ", "Remaining Capacity:10\n", "#### Shelf COLD Contents ####### ",
                "Remaining Capacity:9", "2132e|A|cold|1.0\n", "#### Shelf HOT Contents ####### ",
                "Remaining Capacity:8", "213s|A|hot|1.0", "121sdas|A|hot|1.0\n\n");
        RestaurantShelfManager currentManager = new RestaurantShelfManager();
        ShelfItem order1 = RestaurantOrder.of(createLongOrder("213s", "hot"), new Date());
        ShelfItem order2 = RestaurantOrder.of(createLongOrder("2132e", "cold"), new Date());
        ShelfItem order3 = RestaurantOrder.of(createLongOrder("121sdas", "hot"), new Date());
        Thread t1 = new Thread(() -> {
            try {
                currentManager.addOrderToShelf(order1);
            } catch (ItemExistsException | WrongShelfException e) {
                e.printStackTrace();
            }

        });
        Thread t2 = new Thread(() -> {
            try {
                currentManager.addOrderToShelf(order2);
            } catch (ItemExistsException | WrongShelfException e) {
                e.printStackTrace();
            }

        });

        Thread t3 = new Thread(() -> {
            try {
                currentManager.addOrderToShelf(order3);
            } catch (ItemExistsException | WrongShelfException e) {
                e.printStackTrace();
            }

        });

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();
        String summary = currentManager.getShelvesSummary();

        System.out.println(expected);
        assertEquals(expected , summary);

    }

}
