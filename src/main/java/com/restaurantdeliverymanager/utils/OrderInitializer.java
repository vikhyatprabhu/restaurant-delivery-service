package com.restaurantdeliverymanager.utils;

import java.util.List;

import com.restaurantdeliverymanager.models.Order;
import com.restaurantdeliverymanager.utils.data.DataConfig;
import com.restaurantdeliverymanager.utils.objectmapping.Mapper;
import com.restaurantdeliverymanager.utils.objectmapping.ObjectMappingException;
/**
 * Initialize Orders to a list
 */
public class OrderInitializer {

    Mapper mapper;

    /**
     * Creates an OrderInitializer
     * @param mapper Object mapper to be used
     */
    public OrderInitializer(Mapper mapper){
       this.mapper = mapper;
    }

    /**
     *  Returns initial list of orders parsed
     * @param dataConfig which contains location for fetching the data
     * @return List of orders fetched
     * @throws ObjectMappingException
     */
    public List<Order> getOrders(DataConfig dataConfig) throws ObjectMappingException{
        return this.mapper.<Order>mapDataToListOfObjects(dataConfig , Order.class);
    }

}
