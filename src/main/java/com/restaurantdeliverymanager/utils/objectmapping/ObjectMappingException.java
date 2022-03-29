package com.restaurantdeliverymanager.utils.objectmapping;

/**
 * Thrown when anything cannot be serialized to a given object type
 */
public class ObjectMappingException extends Exception {


    private static final long serialVersionUID = -2508275235208375136L;

    ObjectMappingException(String message){
        super(message);
    }

}
