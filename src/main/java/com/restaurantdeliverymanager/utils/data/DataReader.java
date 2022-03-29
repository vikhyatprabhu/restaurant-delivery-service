package com.restaurantdeliverymanager.utils.data;

/**
 * Read data as string from a data source which could be a file,
 * can be further extended by class which can fetch data from any
 * data source
 */
public interface DataReader {

    /**
     *
     * Read data from a data source
     * @param dataConfig contains the configuration necessary to read the data
     * @return Data is returned as a string
     * @throws DataReaderException
     */
    public String readData(DataConfig dataConfig) throws DataReaderException;

}
