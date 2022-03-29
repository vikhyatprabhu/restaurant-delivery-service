package com.restaurantdeliverymanager.utils.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class FileReaderTest {

    @Test
    public void readFileTest() throws DataReaderException {
        FileReader reader = new FileReader();
        String fileString = reader.readData(new DataConfig("testFile.txt"));
        assertEquals("Please find me", fileString);
    }

    @Test
    public void readFileTestException() throws DataReaderException {
        FileReader reader = new FileReader();
        assertThrows(DataReaderException.class, () -> reader.readData(new DataConfig("testFileNotExists.txt")));
    }

}
