package com.restaurantdeliverymanager.utils.objectmapping;

import java.util.List;

import com.restaurantdeliverymanager.utils.data.DataConfig;

/**
 * Interface for mapping a given data into objects
 */
public interface Mapper {

    /**
     * Map data to list of object type given a dataconfig
     * @param <T>
     * @param dataConfig which contains the location to the data
     * @return List of objects
     * @throws ObjectMappingException
     */
    <T> List<T> mapDataToListOfObjects(DataConfig dataConfig , Class<T> elementClass) throws ObjectMappingException;
}
