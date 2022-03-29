package com.restaurantdeliverymanager.kitchen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

import java.util.List;

import com.restaurantdeliverymanager.models.ShelfItem;
import com.restaurantdeliverymanager.models.ShelfTemperature;
import com.restaurantdeliverymanager.models.exception.ItemExistsException;
import com.restaurantdeliverymanager.models.exception.WrongShelfException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class KitchenShelfTest {

    private RestaurantShelf coldTwoShelf;

    @Mock
    ShelfItem item1;

    @Mock
    ShelfItem item2;

    @Mock
    ShelfItem item3;

    @BeforeEach
    public void setUp() {
        coldTwoShelf = new RestaurantShelf(2, ShelfTemperature.COLD);
    }

    @Test
    public void testAddToShelf() throws ItemExistsException, WrongShelfException {
        coldTwoShelf.add(item1);
        assertTrue(coldTwoShelf.contains(item1));
        assertFalse(coldTwoShelf.isFull());
    }

    @Test
    public void testRemoveFromShelf() throws ItemExistsException, WrongShelfException {
        coldTwoShelf.add(item1);
        coldTwoShelf.remove(item1);
        assertFalse(coldTwoShelf.contains(item1));
        assertFalse(coldTwoShelf.isFull());
    }

    @Test
    public void testShelfFull() throws ItemExistsException, WrongShelfException {
        coldTwoShelf.add(item1);
        coldTwoShelf.add(item2);
        assertTrue(coldTwoShelf.contains(item1));
        assertTrue(coldTwoShelf.contains(item2));
        assertTrue(coldTwoShelf.isFull());
    }

    @Test
    public void testShelfThrowsException() throws ItemExistsException, WrongShelfException {
        coldTwoShelf.add(item1);
        assertThrows(ItemExistsException.class, () -> coldTwoShelf.add(item1));
        assertTrue(coldTwoShelf.contains(item1));
    }

    @Test
    public void testShelfElements() throws ItemExistsException, WrongShelfException {
        coldTwoShelf.add(item1);
        coldTwoShelf.add(item2);
        List<ShelfItem> elements = coldTwoShelf.getElements();
        assertTrue(elements.contains(item1));
        assertTrue(elements.contains(item2));

    }

    @Test
    public void testShelfNotAdded() throws ItemExistsException, WrongShelfException {
        assertTrue(coldTwoShelf.add(item1));
        assertTrue(coldTwoShelf.add(item2));
        assertFalse(coldTwoShelf.add(item3));
    }

    @Test
    public void testShelfProperty() throws ItemExistsException {
        assertEquals(coldTwoShelf.getProperty().getValue(), ShelfTemperature.COLD.getValue());
        assertEquals(coldTwoShelf.getProperty().getLabel(), ShelfTemperature.COLD.getLabel());
    }

    @Test
    public void testShelfSummary() throws ItemExistsException, WrongShelfException {
        String initial="#### Shelf COLD Contents ####### \n";
        doReturn("item1").when(item1).toString();
        doReturn("item2").when(item2).toString();
        assertEquals(coldTwoShelf.getSummary(), initial+"Remaining Capacity:2\n" );
        coldTwoShelf.add(item1);
        assertEquals(coldTwoShelf.getSummary(),initial+"Remaining Capacity:1\n"+"item1\n");
        coldTwoShelf.add(item2);
        assertEquals(coldTwoShelf.getSummary(),initial+"Remaining Capacity:0\n"+"item1\nitem2\n");

    }

}
