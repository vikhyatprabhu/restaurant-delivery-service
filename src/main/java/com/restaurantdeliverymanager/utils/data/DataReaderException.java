package com.restaurantdeliverymanager.utils.data;

/**
 * Thrown when a data read operation fails
 */
public class DataReaderException extends Exception {

    private static final long serialVersionUID = -3238652639618460157L;

    /**
     * Message for the exception
     * @param message
     */
    DataReaderException(String message){
      super(message);
    }

}
