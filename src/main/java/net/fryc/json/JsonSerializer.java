package net.fryc.json;

import java.io.IOException;
import java.util.List;

public interface JsonSerializer<T> {

    void writeToFile(List<T> itemList) throws IOException;

    List<T> readFromFile() throws IOException;
}
