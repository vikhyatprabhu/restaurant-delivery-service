package com.restaurantdeliverymanager.models;

public enum ShelfTemperature implements Property {
    HOT("hot", 1), COLD("cold", 1), FROZEN("frozen", 1), OVERFLOW("overflow", 2);

    private final String label;
    private final int value;

    ShelfTemperature(String label, int value) {
        this.label = label;
        this.value = value;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public String getLabel() {
        return this.label;
    }
}
