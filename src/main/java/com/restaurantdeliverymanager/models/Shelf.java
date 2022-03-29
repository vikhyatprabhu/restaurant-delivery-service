package com.restaurantdeliverymanager.models;

import java.util.List;

import com.restaurantdeliverymanager.models.exception.ItemExistsException;
import com.restaurantdeliverymanager.models.exception.WrongShelfException;

public interface Shelf<T , U> {

    public boolean add(T item) throws ItemExistsException,WrongShelfException;

    public boolean contains(T item);

    public boolean remove(T item);

    public boolean isFull();

    public U getProperty();

    public List<T> getElements();

    public String getSummary();
}
