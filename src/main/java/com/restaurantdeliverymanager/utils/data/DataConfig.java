package com.restaurantdeliverymanager.utils.data;

public class DataConfig {

    private String location;

    public DataConfig(String location){
        this.location = location;
    }

    /**
     * Location represents the path to get the data from
     * for example , the location for a file would be its path, for a DB it could
     * be a connection string
     * @return
     */
    protected String getLocation(){
        return this.location;
    }

}
