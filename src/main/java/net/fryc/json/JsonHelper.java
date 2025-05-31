package net.fryc.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JsonHelper {

    public static final File JSON_FILES_PATH = new File("C:/fryc_data");
    public static final File BOOK_JSON = fileWithDefaultPath("books.json");
    public static final ObjectMapper MAPPER = new ObjectMapper();

    public static void createJsonFilesWhenNeeded(){
        try {
            if(!JSON_FILES_PATH.exists()){
                JSON_FILES_PATH.mkdirs();
            }

            FileWriter writer = new FileWriter(BOOK_JSON);
            writer.write("[]");
            writer.close();
            BOOK_JSON.createNewFile();

        } catch (IOException e) {
            System.out.println("Unable to create JSON files");
            throw new RuntimeException(e);
        }

    }

    public static File fileWithDefaultPath(String name){
        return new File(JSON_FILES_PATH + "/" + name);
    }
}
