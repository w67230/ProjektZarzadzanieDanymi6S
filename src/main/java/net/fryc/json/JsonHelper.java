package net.fryc.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonHelper {

    private static final Map<File, String> ALL_FILES = new HashMap<>();

    public static final File JSON_FILES_PATH = new File("C:/fryc_data");
    public static final File BOOK_JSON = fileWithDefaultPath("books.json", "[]");
    public static final File AUTHOR_JSON = fileWithDefaultPath("authors.json", "[]");
    public static final File USER_JSON = fileWithDefaultPath("users.json", "[]");
    public static final File OFFER_JSON = fileWithDefaultPath("offers.json", "[]");
    public static final File ORDER_JSON = fileWithDefaultPath("orders.json", "[]");
    public static final ObjectMapper MAPPER = new ObjectMapper();


    public static void createJsonFilesWhenNeeded(){
        try {
            if(!JSON_FILES_PATH.exists()){
                JSON_FILES_PATH.mkdirs();
            }

            for(Map.Entry<File, String> entry : ALL_FILES.entrySet()){
                if(!entry.getKey().exists()){
                    FileWriter writer = new FileWriter(entry.getKey());
                    writer.write(entry.getValue());
                    writer.close();
                    entry.getKey().createNewFile();
                }
            }
        }
        catch (IOException e) {
            System.out.println("Unable to create JSON files");
            throw new RuntimeException(e);
        }

    }

    /**
     *  fileBeginning is used when file doesn't exist and needs to be created
     */
    private static File fileWithDefaultPath(String name, String fileBeginning){
        File file = new File(JSON_FILES_PATH + "/" + name);
        ALL_FILES.put(file, fileBeginning);
        return file;
    }
}
