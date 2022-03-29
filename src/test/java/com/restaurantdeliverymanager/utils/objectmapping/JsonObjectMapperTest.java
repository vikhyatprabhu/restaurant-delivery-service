package com.restaurantdeliverymanager.utils.objectmapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.restaurantdeliverymanager.models.Order;
import com.restaurantdeliverymanager.utils.data.DataConfig;
import com.restaurantdeliverymanager.utils.data.DataReaderException;
import com.restaurantdeliverymanager.utils.data.FileReader;

import org.junit.jupiter.api.Test;

public class JsonObjectMapperTest {

    @Test
    public void testListWith1Element() throws DataReaderException, ObjectMappingException {
        JsonObjectMapper mapper = new JsonObjectMapper(new FileReader());
        List<Order> orders = mapper.<Order>mapDataToListOfObjects(new DataConfig("orders.json"), Order.class);
        assertTrue(orders.size() == 1);
    }

    @Test
    public void testListWithManyElements() throws DataReaderException, ObjectMappingException {
        JsonObjectMapper mapper = new JsonObjectMapper(new FileReader());
        List<Order> orders = mapper.<Order>mapDataToListOfObjects(new DataConfig("ordersSmall.json"), Order.class);
        assertEquals(11, orders.size());
    }

    @Test
    public void testListInvalid() throws DataReaderException {
        JsonObjectMapper mapper = new JsonObjectMapper(new FileReader());
        assertThrows(ObjectMappingException.class,
                () -> mapper.<Order>mapDataToListOfObjects(new DataConfig("ordersInvalid.json"), Order.class));

    }

}
