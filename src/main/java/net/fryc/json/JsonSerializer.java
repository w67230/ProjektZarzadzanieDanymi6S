package net.fryc.json;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface JsonSerializer<T> {

    void writeToFile(File file, List<T> itemList) throws IOException;

    List<T> readFromFile() throws IOException;
}
