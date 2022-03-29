package com.restaurantdeliverymanager.utils.objectmapping;

import java.util.ArrayList;
import java.util.List;

import com.restaurantdeliverymanager.utils.data.DataConfig;
import com.restaurantdeliverymanager.utils.data.DataReader;
import com.restaurantdeliverymanager.utils.data.DataReaderException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

public class JsonObjectMapper implements Mapper {

    private DataReader reader;
    private ObjectMapper objectMapper;

    /**
     * Creates a Json Mapper object
     * @param reader which returns a string from a given data source
     */
    public JsonObjectMapper(DataReader reader) {
        this.reader = reader;
        this.objectMapper = new ObjectMapper();
    }

    public <T> List<T> mapDataToListOfObjects(DataConfig dataConfig , Class<T> elementClass) throws ObjectMappingException {
        try {
            CollectionType listType =objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, elementClass);
            return objectMapper.readValue(this.reader.readData(dataConfig), listType);
        } catch (JsonProcessingException | DataReaderException ex) {
            throw new ObjectMappingException("Object Mapping Exception:" + ex.getMessage());
        }
    }



}
