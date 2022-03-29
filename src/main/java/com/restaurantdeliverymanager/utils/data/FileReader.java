package com.restaurantdeliverymanager.utils.data;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileReader implements DataReader {

    @Override
    public String readData(DataConfig dataConfig) throws DataReaderException {
        try {
            URL url = getClass().getClassLoader().getResource(dataConfig.getLocation());
            if(url == null){
                throw new DataReaderException("Resource not found");
            }
            URI uri = url.toURI();
            Path path = Paths.get(uri);
            Stream<String> lines = Files.lines(path);
            String data = lines.collect(Collectors.joining("\n"));
            lines.close();
            return data;
        } catch (URISyntaxException | IOException ex) {
            throw new DataReaderException(ex.getMessage());
        }

    }

}
